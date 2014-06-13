<script type="application/javascript">
    $(document).ready(function () {
        function changeYearSelected() {
            var tournamentId = $('#TournamentId').val();
            $.ajax({
                url: '/admin/tournaments/review/' + tournamentId,
                success: function (response) {
                    var tournamentReviewElement = '#TournamentReview';
                    $(tournamentReviewElement).html($(response).find(tournamentReviewElement).html());
                }
            });
        }

        $('#TournamentId').change(function () {
            changeYearSelected();
        });
    });
</script>

<div id="box" style="text-align: center; background-color: #3F2828; margin-bottom: 20px;">
    <h1>Edit a Tournament Review</h1>
</div>

<?php echo $this->Form->create('Tournament'); ?>
<span style="font-size: 14pt; color: black;">Select the year of the tournament whose review you want to edit: </span>
<?php echo $this->Form->select('id', $tournamentList, array('empty' => false, 'value' => $tournament['Tournament']['id'])); ?>
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