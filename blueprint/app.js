const express = require('express');
const fs = require("fs")
const path = require('path')
const bodyParser = require('body-parser');
const router = require('./router')
const session = require('express-session')
const cookieParser = require("cookie-parser")
const bearerToken = require('express-bearer-token');
const flash = require('express-flash')

const app = express();

const middleware = [
    express.static('public'),
    bodyParser.json(),
    bodyParser.urlencoded({ extended: true }),
    bearerToken(),
    cookieParser(),
    flash(),
    session({
        cookie: {
            maxAge: 9000000
        },
        secret: '24svhxzfasdfsxlcviuyl23h423kjh',
        resave: false,
        saveUninitialized: false
    })
]


app.use(middleware)

express.static('public')
//app.use('/uploaded_images', express.static('uploaded_images'))
app.set('views', path.join(__dirname, 'views'))
app.set('view engine', 'ejs')
app.use('/', router)

app.use((req, res, next) => {
    res.status(404).render("error", {
        code: "404",
        reason: "Page Not Found",
        description: "The page you are looking for does not exist."
    })
})

const port = 8080

app.listen(port, () => console.log(`App listening on port ${port}`));