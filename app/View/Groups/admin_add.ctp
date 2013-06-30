<div id="boxFloat" style="background-color:#29110D; text-align:center; width:284px">
	<?php
		$numGroup = array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');
		$currGroup = 0;

		echo $this->Form->create('Group');

		for($i = 1; $i <= 32; $i++) {
			if(($i - 1) % 4 == 0) {
				echo '<b>Group ' . $numGroup[$currGroup] . '</b>';
			}

			echo $this->Form->input('player' . $i, array(
				'empty' => true,
				'options' => $users,
				'label' => 'Player ' . $i,
				'style' => 'margin:5px;'
			));

			if($i % 4 == 0) {
				$currGroup++;
				echo '</div><div id="boxFloat" style="background-color:#29110D; text-align:center; width:284px">';
			}
		}

        echo $this->Form->submit('Submit', array(
            'onclick' => 'return confirm(\'Are you sure? Have you checked the correctness of the group draw?\');'
        ));

		echo $this->Form->end();
	?>
</div>

<div style="clear:both; height:10px"></div>
