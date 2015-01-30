<?php
App::uses('AppModel', 'Model');

class Stream extends AppModel
{
    public $displayField = 'title';
    public $name = 'Stream';

    public $belongsTo = array(
        'User' => array(
            'className' => 'User',
            'foreignKey' => 'maintainer_id'
        )
    );

    /**
     * Queries the TwitchTV API.
     *
     * @param $method String GET or POST?
     * @param $url String The URL to send to will be prepended with https://api.twitch.tv/kraken/
     * @param bool $data Date to be sent with the request.
     * @return Array The JSON response converted to an array.
     */
    public function callTwitchApi($method, $url, $data = false)
    {
        $url .= 'https://api.twitch.tv/kraken/';
        $curl = curl_init();

        switch ($method)
        {
            case "POST":
                curl_setopt($curl, CURLOPT_POST, 1);

                if ($data)
                    curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
                break;
            case "PUT":
                curl_setopt($curl, CURLOPT_PUT, 1);
                break;
            default:
                if ($data)
                    $url = sprintf("%s?%s", $url, http_build_query($data));
        }

        // Optional Authentication:
        curl_setopt($curl, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
        curl_setopt($curl, CURLOPT_USERPWD, "username:password");

        curl_setopt($curl, CURLOPT_URL, $url);
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);

        $result = curl_exec($curl);

        curl_close($curl);

        return json_decode($result, true);
    }

    // Current user is maintainer of a stream? Any stream online?
    public function checkings()
    {
        $maintainer = $this->find('first', array(
            'conditions' => array(
                'Stream.maintainer_id' => AuthComponent::user('id')
            )
        ));

        if (empty($maintainer)) {
            return false;
        }

        $online = $this->find('first', array(
            'conditions' => array(
                'Stream.online' => true
            )
        ));


        $onlineStreamId = empty($online) ? $online : $online['Stream']['id'];

        return array(
            'maintainer' => $maintainer['Stream'],
            'online' => $onlineStreamId
        );
    }

    // Converts RGB definiton to a Hexadecimal.
    public function rgb2hex($rgbColor)
    {
        $rgb = explode(', ', substr($rgbColor, 4, -1));

        $hex = str_pad(dechex($rgb[0]), 2, "0", STR_PAD_LEFT);
        $hex .= str_pad(dechex($rgb[1]), 2, "0", STR_PAD_LEFT);
        $hex .= str_pad(dechex($rgb[2]), 2, "0", STR_PAD_LEFT);

        return $hex;
    }

    public $validate = array(
        'title' => array(
            'notEmpty' => array(
                'rule' => 'notEmpty',
                'message' => 'Please give your stream a title.'
            ),
            'isUnique' => array(
                'rule' => 'isUnique',
                'message' => 'This title is already taken.
					Please think of another one.'
            )
        ),
        'embedcode' => array(
            'notEmpty' => array(
                'rule' => 'notEmpty',
                'message' => 'Your stream needs an embedded video player.'
            ),
            'isUnique' => array(
                'rule' => 'isUnique',
                'message' => 'This embedding code is already in use.'
            ),
            'isEmbeddingCode' => array(
                'rule' => 'isEmbeddingCode',
                'message' => 'That\'s not a valid embedding code.'
            )
        )
    );

    // Actually I'm just checking whether there are some
    // HTML-typical characters. Better than nothing, huh?
    public function isEmbeddingCode($data)
    {
        preg_match_all('[<|>|"|=]', $data['embedcode'], $result);

        if (!in_array('<', $result[0])) {
            return false;
        }

        if (!in_array('>', $result[0])) {
            return false;
        }

        if (!in_array('"', $result[0])) {
            return false;
        }

        if (!in_array('=', $result[0])) {
            return false;
        }

        return true;
    }
}
