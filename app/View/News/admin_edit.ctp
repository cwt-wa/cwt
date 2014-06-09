<?php
echo $this->Html->script('bbcode', array('inline' => false));
echo $this->Html->css('bbcode', null, array('inline' => false));
?>

<?php
echo $this->element('bbcode', array(
    'style' => 'width:958px; height:500px;',
    'destination' => '/admin/news/edit',
    'value' => $news['News']['text']
));
?>
