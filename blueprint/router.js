const express = require('express');
const path = require('path')
var fs = require('fs');
var uuid = require('uuid/v1')
var sqlite3 = require('sqlite3').verbose();
const jwt = require('jsonwebtoken')

const crypto = require('crypto');

const util = require('./util')
const constants = require('./constants')

const router = express.Router();

const privateKey = fs.readFileSync('./private.key', 'utf8');
const publicKey = fs.readFileSync('./public.key', 'utf8');
 
var signOptions = {
    issuer: 'Edgemont Jr./Sr. High School',
    subject: 'sdlfkjsdf',
    audience: 'https://ehs.edgemont.org',
    expiresIn: '12h',
    algorithm: 'RS256'
};

function setCookie(token, res) {
    const cookieOptions = {
        httpOnly: true,
        maxAge: 9000000
    }

    res.cookie(constants.token, token, cookieOptions)
}

var file = path.join(__dirname, '/database.db')
var exists = fs.existsSync(file);

if (!exists) {
    console.log('Creating DB file');
    fs.openSync(file, 'w');
}

const tableName = "sessions"

var db = new sqlite3.Database(file);

if (!exists) {
    db.run(`CREATE TABLE ${tableName} (id integer primary key, code integer, salt TEXT, encrypted_password TEXT, json_data TEXT)`)
}

function checkAuth(code, req, res, next) {
    const token = req.cookies.accessToken

    console.log("Cookies: ")
    console.log(req.cookies)

    if (!token) {
        try {
            res.clearCookie(constants.token, {
                path: '/'
            })
        } catch (e) {
            //don't log http headers set error
        }
        next(false)
        return;
    }
    jwt.verify(token, publicKey, (e, p) => {
        if (e) {
            try {
                res.clearCookie(constants.token, {
                    path: '/'
                })
            } catch (e) {
                //don't log http headers set error
            }
            next(false)
        } else {
            db.get(`SELECT * FROM ${tableName} WHERE code = ?`, code, (e, r) => {
                if(e || !r){
                    res.json({error: true, message: "id not found"})
                    return;
                }
    
                var hash = saltHashPassword(p.password, r.salt)
    
                if(hash != r.encrypted_password){
                    next(false)
                }else{
                    next(true)
                }
            })
        }
    })
}

router.get('/', function (req, res) {
    res.render('join.ejs')
})

router.get('/create', function(req, res) {
    res.render('create')
})

function generateUnusedRandom(next){
    var id = constants.random(0, 1000000)

    db.get(`select * from ${tableName} where code = ?`, id, function(e, r) {
        if(!r){
            next(id)
        }else{
            generateUnusedRandom(next)
        }
    })
}

function saltHashPassword(userpassword, salt) {
    var passwordData = util.sha512(userpassword, salt);
    console.log('UserPassword = '+userpassword);
    console.log('Passwordhash = '+passwordData.passwordHash);
    console.log('nSalt = '+passwordData.salt);

    return passwordData.passwordHash
}

router.post('/create_session', function(req, res) {
    generateUnusedRandom(function(id){
        console.log(req.body)

        var salt = util.genRandomString(16); /** Gives us salt of length 16 */

        var encrypted = saltHashPassword(req.body.psw, salt)

        db.run(`insert into '${tableName}' (code, salt, encrypted_password, json_data) values (?, ?, ?, ?)`, 
        [id, salt, encrypted, `{"dest": "", "people": []}`])

        var payload = {
            password: req.body.psw
        }
    
        const token = jwt.sign(payload, privateKey, signOptions);
        
        setCookie(token, res)

        res.json({
            id: id
        })
        //res.redirect(`/session/${id}`)
    })
})

router.post('/login', function(req, res){
    console.log(req.body)

    var payload = {
        password: req.body.psw
    }

    var signOptions = {
        issuer: 'Edgemont Jr./Sr. High School',
        subject: 'sdlfkjsdf',
        audience: 'https://ehs.edgemont.org',
        expiresIn: '12h',
        algorithm: 'RS256'
    };

    const token = jwt.sign(payload, privateKey, signOptions);

    setCookie(token, res)

    res.redirect(`/session/${req.body.uname}`)
})

router.get('/session/:id', function(req, res) {
    const id = req.params.id;

    checkAuth(id, req, res, function(success){
        if(!success){
            res.json({
                success: false,
                message: "Password Incorrect"
            })
            return;
        } 
        db.get(`select * from ${tableName} where code = ?`, id, function(e, r) {
            if(e || !r){
                res.json({
                    success: false,
                    message: "Session info not found :'("
                })
            }else{
                res.json({
                    success: true,
                    json: JSON.parse(r.json_data)
                })
            }
        })
    })
})

router.get('/calculate/:id', function(req, res) {
    const id = req.params.id;

    checkAuth(id, req, res, function(success){
        if(!success){
            res.send({success: false, message: "Not signed in"})
            return;
        }

        db.get(`select * from ${tableName} where code = ?`, id, function(e, r){
            if(e){
                res.send({success: false, message: "Doesn't exist"})
                return;
            }

            console.log(r.json_data)
            var child = require('child_process').exec(
                `java -jar ./public/run.jar "${r.json_data}"`, function(error, stdout, stderr) {
                    console.log(stderr)
                    console.log(error)
                    res.send(stdout)
                }
              );
        })
    })
})

router.post('/update_session/:id', function(req, res) {
    const id = req.params.id;

    checkAuth(id, req, res, function(success){
        if(!success){
            res.json({
                success: false,
                message: 'Not logged in!'
            })
            return;
        }
        
        console.log("Body: ")
        console.log(req.body.json)
        console.log("end of body")

        db.run(`update ${tableName} set json_data = ? where code = ?`, [JSON.stringify(req.body.json), id], function(e) {
            if(e){
                res.json({
                    success: false,
                    message: "Session info not found :'("
                })
            }else{
            }
        })

        db.get(`select * from ${tableName} where code = ?`, id, function(e, r){
            if(e || !r){
                res.json({
                    success: false,
                    message: "Session info not found :'("
                })
                return;
            }
            console.log(r)
            if(r){
                res.json({
                    success: true,
                    json: JSON.parse(r.json_data)
                })
            }
        })
    })
})

module.exports = router;