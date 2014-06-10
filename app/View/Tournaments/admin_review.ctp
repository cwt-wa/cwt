<div id="box" style="text-align: center; background-color: #3F2828; margin-bottom: 20px;">
    <h1>Review for CWT <?php echo $tournament['Tournament']['year'] ?></h1>
</div>

<?php echo $this->Form->create('Tournament'); ?>
<?php
echo $this->Form->input('review', array(
    'value' => $tournament['Tournament']['review'],
    'label' => false,
    'style' => 'width: 100%; height: 300px; paddi     ng: 15px;'
));
?>
<?php
echo $this->Form->end('Submit Review');
?>