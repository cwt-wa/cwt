<script type="application/javascript">
    $(document).ready(function () {
        function changeYearSelected() {
            var tournamentId = $('#TournamentYear').val();
            $.ajax({
                url: '/admin/tournaments/review/' + tournamentId,
                success: function (response) {
                    var tournamentReviewElement = '#TournamentReview';
                    $(tournamentReviewElement).html($(response).find(tournamentReviewElement).html());
                }
            });
        }

        $('#TournamentYear').change(function () {
            changeYearSelected();
        });
    });
</script>

<div id="box" style="text-align: center; background-color: #3F2828; margin-bottom: 20px;">
    <h1>Review for CWT <?php echo $tournament['Tournament']['year'] ?></h1>
</div>

<?php echo $this->Form->create('Tournament'); ?>
<?php echo $this->Form->select('year', $tournamentList, array('empty' => false)); ?>
<?php
echo $this->Form->input('review', array(
    'value' => $tournament['Tournament']['review'],
    'label' => false,
    'style' => 'width: 100%; height: 300px; padding: 15px;'
));
?>
<?php
echo $this->Form->end('Submit Review');
?>