import {ConvertLinksPipe} from "../../../../main/webapp/app/_util/convert-links.pipe";

describe('ConvertLinksPipe can convert a string', () => {
    const pipe = new ConvertLinksPipe();

    it('containing nothing more than a link', () => {
        expect(pipe.transform("www.cwtsite.com"))
            .toEqual('<a href="http://www.cwtsite.com" target="_blank">www.cwtsite.com</a>')
    });

    it('with a link surrounded by strings.', () => {
        expect(pipe.transform("The greatest page in the world is www.cwtsite.com I swear!"))
            .toEqual('The greatest page in the world is <a href="http://www.cwtsite.com" target="_blank">www.cwtsite.com</a> I swear!')
    });

    it('containing http or www or https.', () => {
        expect(pipe.transform("A page called http://cwtsite.com is a nice page."))
            .toEqual('A page called <a href="http://cwtsite.com" target="_blank">http://cwtsite.com</a> is a nice page.',
                'http failed');

        expect(pipe.transform("A page called www.cwtsite.com is a nice page."))
            .toEqual('A page called <a href="http://www.cwtsite.com" target="_blank">www.cwtsite.com</a> is a nice page.',
                'www failed');

        expect(pipe.transform("A page called https://cwtsite.com is a nice page."))
            .toEqual('A page called <a href="https://cwtsite.com" target="_blank">https://cwtsite.com</a> is a nice page.',
                'https failed');

        expect(pipe.transform("A page called http://www.cwtsite.com is a nice page."))
            .toEqual('A page called <a href="http://www.cwtsite.com" target="_blank">http://www.cwtsite.com</a> is a nice page.',
                'http and www failed');

        expect(pipe.transform("A page called https://www.cwtsite.com is a nice page."))
            .toEqual('A page called <a href="https://www.cwtsite.com" target="_blank">https://www.cwtsite.com</a> is a nice page.',
                'https and www failed')
    });

    it('containing a link with multiple subdomains.', () => {
        expect(pipe.transform("A page called http://www.2009.cwtsite.com is a nice page."))
            .toEqual('A page called <a href="http://www.2009.cwtsite.com" target="_blank">http://www.2009.cwtsite.com</a> is a nice page.');
    });

    it('containing a link as the last part of a sentence.', () => {
        expect(pipe.transform("I really like https://cwtsite.com."))
            .toEqual('I really like <a href="https://cwtsite.com" target="_blank">https://cwtsite.com</a>.');
    });

    it('containing a link preceding a comma.', () => {
        expect(pipe.transform("I really like https://cwtsite.com, it's true."))
            .toEqual('I really like <a href="https://cwtsite.com" target="_blank">https://cwtsite.com</a>, it\'s true.');
    });

    it('containing an email address', () => {
        expect(pipe.transform("When I have question, I write to support@cwtsite.com and get answers."))
            .toEqual('When I have question, I write to <a href="mailto:support@cwtsite.com" target="_blank">support@cwtsite.com</a> and get answers.');
    });

    it('containing an email address and a link', () => {
        expect(pipe.transform("Emails at support@cwtsite.com and internet at http://cwtsite.com for much fun."))
            .toEqual('Emails at <a href="mailto:support@cwtsite.com" target="_blank">support@cwtsite.com</a> and internet at <a href="http://cwtsite.com" target="_blank">http://cwtsite.com</a> for much fun.');
    });

    // TODO Current failures:
    //
    // 'A page called http://www.<a href="http://2009.cwtsite.com" target="_blank">2009.cwtsite.com</a> is a nice page.'
    // 'A page called <a href="http://www.2009.cwtsite.com" target="_blank">http://www.2009.cwtsite.com</a> is a nice page.'.
    //
    // 'Emails at support@<a href="http://cwtsite.com" target="_blank">cwtsite.com</a> and internet at <a href="http://cwtsite.com" target="_blank">http://cwtsite.com</a> for much fun.'
    // 'Emails at <a href="mailto:support@cwtsite.com" target="_blank">support@cwtsite.com</a> and internet at <a href="http://cwtsite.com" target="_blank">http://cwtsite.com</a> for much fun.'.
    //
    // 'When I have question, I write to support@<a href="http://cwtsite.com" target="_blank">cwtsite.com</a> and get answers.'
    // 'When I have question, I write to <a href="mailto:support@cwtsite.com" target="_blank">support@cwtsite.com</a> and get answers.'.
});
