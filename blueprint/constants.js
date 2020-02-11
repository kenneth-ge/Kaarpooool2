module.exports = {
    token: 'accessToken',
    random: function(min, max) {
        return Math.floor(Math.random() * (max - min) + min)
    }
}
