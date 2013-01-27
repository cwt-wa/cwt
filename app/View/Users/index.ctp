<?php if(!@$current_user['logged_in']): ?>
	<div id="box" style="background-color:#161C1D; text-align:center;">
		You may not see all players when you are not logged in as some have hidden their profiles from public.
	</div>
<?php endif; ?>
<div id="box" style="background-color:#0A0E1C">
	<table border="0" cellspace="0" cellpadding="5" align="center">
		<tr>
		    <td style="border-bottom:1px solid #2E2E2E; font-size:12pt;" align="center" width="50">
		        <?php echo $this->Paginator->sort('Profile.country', 'Country') ?>
		    </td>
		    <td style="border-bottom:1px solid #2E2E2E; font-size:12pt;" align="center" width="50">
		        <?php echo $this->Paginator->sort('Profile.clan', 'Clan') ?>
		    </td>
		    <td style="border-bottom:1px solid #2E2E2E; font-size:12pt;" align="left" width="140">
		        <?php echo $this->Paginator->sort('username') ?>
		    </td>
		    <td style="border-bottom:1px solid #2E2E2E; font-size:12pt;" align="center">
		        <?php echo $this->Paginator->sort('participations') ?>
		    </td>
		    <td style="border-bottom:1px solid #2E2E2E; font-size:12pt;" align="center">
		        <?php echo $this->Paginator->sort('achievements') ?>
		    </td>
		    <td style="border-bottom:1px solid #2E2E2E; font-size:12pt;" align="center" width="120">
		        <b>Contact</b>
		    </td>
		</tr>

		<?php foreach($users as $user): ?>
			<tr>
		        <td style="border-bottom:1px solid #2E2E2E" align="center">
		            <?php 
                        echo $this->Html->image('flags/' . str_replace(' ', '_', strtolower($user['Profile']['country'])) . '.png', array(
                        	'alt' => $user['Profile']['country'],
                        	'title' => $user['Profile']['country']
                        ));
                    ?>
		        </td>
		        <td style="border-bottom:1px solid #2E2E2E" align="center">
		            <?php echo $user['Profile']['clan'] ?>
		        </td>
		        <td style="border-bottom:1px solid #2E2E2E" align="left">
		            <?php 
                        echo $this->Html->link($user['User']['username'], 
                            '/users/view/' . $user['User']['id'],
                            array(
                                'style' => 'color:white;'
                        )); 
                    ?>
		        </td>
		         <td style="border-bottom:1px solid #2E2E2E" align="center">
		         	<?php echo $user['User']['participations']; ?>
		         </td>
		        <td style="border-bottom:1px solid #2E2E2E" align="center">
		            <?php 
		            	foreach($achievements as $key => $val) {		            	
		            		if($val['gold'] == $user['User']['id']) {
		            			echo $this->Html->image('medals/' . array_search($val['gold'], $val) . '.gif', array(
			            				'alt' => $key,
			            				'title' => $key,
			            				'style' => 'height:15px; width:auto;'
			            			)
			            		) . '&nbsp;';
		            		} elseif($val['silver'] == $user['User']['id']) {
		            			echo $this->Html->image('medals/' . array_search($val['silver'], $val) . '.gif', array(
			            				'alt' => $key,
			            				'title' => $key,
			            				'style' => 'height:15px; width:auto;'
			            			)
			            		) . '&nbsp;';
		            		} elseif($val['bronze'] == $user['User']['id']) {
		            			echo $this->Html->image('medals/' . array_search($val['bronze'], $val) . '.gif', array(
			            				'alt' => $key,
			            				'title' => $key,
			            				'style' => 'height:15px; width:auto;'
			            			)
			            		) . '&nbsp;';
		            		} 	            	
		        		}
		        	?>
		        </td>
		        <td style="border-bottom:1px solid #2E2E2E" align="center">
		            <?php if($user['Profile']['msn']): ?>
		            	<?php 
		            		echo $this->Html->link($this->Html->image('contact/msn.PNG'),
		            			'http://members.msn.com/' . $user['Profile']['msn'], array(
								 	'alt' => $user['Profile']['msn'],
								 	'title' => $user['Profile']['msn'],
								 	'escape' => false,
								 	'target' => '_blank'
		            			)
		            		)
		            	?>
		            <?php endif; ?>
		            <?php if($user['Profile']['icq']): ?>
		            	<?php 
		            		echo $this->Html->link($this->Html->image('contact/icq.PNG'),
		            			'http://icq.com/people/' . $user['Profile']['icq'], array(
								 	'alt' => $user['Profile']['icq'],
								 	'title' => $user['Profile']['icq'],
								 	'escape' => false,
								 	'target' => '_blank'
		            			)
		            		)
		            	?>
		            <?php endif; ?>
		            <?php if($user['Profile']['email']): ?>
		            	<?php 
		            		echo $this->Html->link($this->Html->image('contact/email.PNG'),
		            			'mailto:' . $user['Profile']['email'], array(
								 	'alt' => $user['Profile']['email'],
								 	'title' => $user['Profile']['email'],
								 	'escape' => false
		            			)
		            		)
		            	?>
		            <?php endif; ?>
		            <?php if($user['Profile']['facebook']): ?>
		            	<?php 
		            		echo $this->Html->link($this->Html->image('contact/facebook.PNG'),
		            			$user['Profile']['facebook'], array(
								 	'alt' => $user['Profile']['facebook'],
								 	'title' => $user['Profile']['facebook'],
								 	'escape' => false,
								 	'target' => '_blank'
		            			)
		            		)
		            	?>
		            <?php endif; ?>
		            <?php if($user['Profile']['googlep']): ?>
		            	<?php 
		            		echo $this->Html->link($this->Html->image('contact/googlep.PNG'),
		            			$user['Profile']['googlep'], array(
								 	'alt' => $user['Profile']['googlep'],
								 	'title' => $user['Profile']['googlep'],
								 	'escape' => false,
								 	'target' => '_blank'
		            			)
		            		)
		            	?>
		            <?php endif; ?>
		            <?php if($user['Profile']['twitter']): ?>
		            	<?php 
		            		echo $this->Html->link($this->Html->image('contact/twitter.PNG'),
		            			$user['Profile']['twitter'], array(
								 	'alt' => $user['Profile']['twitter'],
								 	'title' => $user['Profile']['twitter'],
								 	'escape' => false,
								 	'target' => '_blank'
		            			)
		            		)
		            	?>
		            <?php endif; ?>
		        </td>
		    </tr>
		<?php endforeach; ?>
	</table>

	<center>
		<p>
		<?php
			echo $this->Paginator->counter(array(
			'format' => __('Page {:page} of {:pages}, showing {:current} records out of {:count} total, starting on record {:start}, ending on {:end}')
			));
		?>
		</p>
	</center>

	<div class="paging">
	<?php
		echo $this->Paginator->prev('< ' . __('previous'), array(), null, array('class' => 'prev disabled'));
		echo $this->Paginator->numbers(array('separator' => ''));
		echo $this->Paginator->next(__('next') . ' >', array(), null, array('class' => 'next disabled'));
	?>
	</div>
</div>