<?php

App::uses('AppHelper', 'View/Helper');

class BbcodeHelper extends AppHelper
{
    public $bbcodes = array(
        'b' => '/(\[[Bb]\])(.+?)(\[\/[Bb]\])/s',
        'i' => '/(\[[Ii]\])(.+?)(\[\/[Ii]\])/s',
        'u' => '/(\[[Uu]\])(.+?)(\[\/[Uu]\])/s',
        's' => '/(\[[Ss]\])(.+?)(\[\/[Ss]\])/s',
        'size' => '/(\[size=)(.+?)(\])(.+?)(\[\/size\])/s',
        'color' => '/(\[color=)(.+?)(\])(.+?)(\[\/color\])/s',
        'center' => '/(\[center\])(.+?)(\[\/center\])/s',
        'url2' => '/(\[url=)(.+?)(\])(.+?)(\[\/url\])/s',
        'url' => '/(\[url\])(.+?)(\[\/url\])/s',
        'img' => '/(\[img\])(.+?)(\[\/img\])/s',
        'img2' => '/(\[img=)(.+?)([xX])(.+?)(\])(.+?)(\[\/img\])/s',
        'quote' => '/(\[quote\])(.+?)(\[\/quote\])/s',
        'ul' => '/(\[ul\])(.+?)(\[\/ul\])/s',
        'ol' => '/(\[ol\])(.+?)(\[\/ol\])/s',
        'li' => '/(\[li\])(.+?)(\[\/li\])/s',
        'code' => '/(\[code\])(.+?)(\[\/code\])/s'
    );

    public $htmlcodes = array(
        'b' => '<b>\\2</b>',
        'i' => '<i>\\2</i>',
        'u' => '<u>\\2</u>',
        's' => '<strike>\\2</strike>',
        'size' => '<span style="font-size:\\2pt;">\\4</span>',
        'color' => '<font color="\\2">\\4</font>',
        'center' => '<center>\\2</center>',
        'url2' => '<a href="\\2" target="_blank">\\4</a>',
        'url' => '<a href="\\2" target="_blank">\\2</a>',
        'img' => '<img src="\\2">',
        'img2' => '<img width="\\2" height="\\4" src="\\6">',
        'quote' => '<blockquote>\\2</blockquote>',
        'ul' => '<ul>\\2</ul>',
        'ol' => '<ol>\\2</ol>',
        'li' => '<li>\\2</li>',
        'code' => '<pre>\\2</pre>'
    );

    public function parse($text)
    {
        $parsed = preg_replace($this->bbcodes, $this->htmlcodes, $text);
        return $this->output($parsed);
    }

    public $allowed = array(
        'b' => '/(&lt;b&gt;)(.+?)(&lt;\/b&gt;)/s',
        'i' => '/(&lt;i&gt;)(.+?)(&lt;\/i&gt;)/s',
        'u' => '/(&lt;u&gt;)(.+?)(&lt;\/u&gt;)/s',
        's' => '/(&lt;strike&gt;)(.+?)(&lt;\/strike&gt;)/s',
        'size' => '/(&lt;span style=&quot;font-size:)(.+?)(pt;&quot;&gt;)(.+?)(&lt;\/span&gt;)/s',
        'color' => '/(&lt;font color=&quot;)(.+?)(&quot;&gt;)(.+?)(&lt;\/font&gt;)/s',
        'center' => '/(&lt;center&gt;)(.+?)(&lt;\/center&gt;)/s',
        'url2' => '/(&lt;a href=&quot;)(.+?)(&quot; target=&quot;_blank&quot;&gt;)(.+?)(&lt;\/a&gt;)/s', // that's the same.
        'url' => '/(&lt;a href=&quot;)(.+?)(&quot; target=&quot;_blank&quot;&gt;)(.+?)(&lt;\/a&gt;)/s', // Uh yeah,
        'img' => '/(&lt;img src=&quot;)(.+?)(&quot;&gt;)/s',
        'img2' => '/(\[img=)(.+?)([xX])(.+?)(\])(.+?)(\[\/img\])/s',
        'quote' => '/(&lt;blockquote&gt;)(.+?)(&lt;\/blockquote&gt;)/s',
        'ul' => '/(&lt;ul&gt;)(.+?)(&lt;\/ul&gt;)/s',
        'ol' => '/(&lt;ol&gt;)(.+?)(&lt;\/ol&gt;)/s',
        'li' => '/(&lt;li&gt;)(.+?)(&lt;\/li&gt;)/s',
        'code' => '/(&lt;pre&gt;)(.+?)(&lt;\/pre&gt;)/s'
    );
}
