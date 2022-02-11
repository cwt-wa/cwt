module.exports = {
    common: {
        'nameLong': "Crespo’s Worms Tournament",
        'nameShort': "CWT",
    },
    dev: {
        'apiEndpoint': 'http://localhost:9000/api/',
        'captchaKey': '6LdAgLYUAAAAAJp86PhBUHQA33EQeJrDHBi-iWNR',
        'liveStreamProducer': 'https://twitch.cwtsite.com/produce',
        'liveStreamSubscriber': 'https://twitch.cwtsite.com/subscribe',
        'twitchBotEndpoint':  'http://localhost:1234',
    },
    prod: {
        'ENV': process.env.NODE_ENV = process.env.ENV = 'production',
        'apiEndpoint': "/api/",
        'captchaKey': '6LcWgLYUAAAAAOvJrsE-KX2ZZNgHqkd9tBwm-tq4',
        'liveStreamProducer': 'https://twitch.cwtsite.com/produce',
        'liveStreamSubscriber': 'https://twitch.cwtsite.com/subscribe',
        'twitchBotEndpoint':  'https://twitch-bot.cwtsite.com',
    },
};

