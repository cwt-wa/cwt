<?php
echo $this->Html->script('bbcode', array('inline' => false));
echo $this->Html->css('bbcode', null, array('inline' => false));
?>
<div id="box" style="background-color:#3F2828; text-align:center;">
    <h1>Admin News</h1>
</div>
<div id="box" style="background-color:#3F2828; text-align:center; padding:5px; font-size:8pt">
    <b>ProTip!</b> And empty submission will display a default welcome message.
</div>
<div id="box" style="background-color:#3F2828; text-align:center; padding:5px; font-size:8pt">
    <b>ProTip!</b> You can quickly link to users with their name like <a href="http://cwtsite.com/users/view/Zemke" target="_blank">http://cwtsite.com/users/view/Zemke</a>. This is case-insensitive.
</div>
<?php
echo $this->element('bbcode', array(
    'style' => 'width:958px; height:500px;',
    'destination' => '/admin/news/edit',
    'value' => $news['News']['text']
));
?>
