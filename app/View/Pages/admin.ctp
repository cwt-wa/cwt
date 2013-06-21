<?php if($current_user['admin']): ?>
	<div id="box" style="background-color:#3F2828; text-align:center;">
		<h1>Admin Panel</h1>
	</div>
	<br>
	<div id="boxFloat" style="background-color:#3F2828; width:475px;">
		<ul>
			<li>
				<a href="/admin/news/edit">Write Admin News</a>
			</li>
			<li>
				<a href="/admin/tournaments/upload">Admin Files</a>
			</li>
			<li>
				<a href="/pages/logs?log=login">Login Log</a>
			</li>
			<li>
				<a href="/pages/logs?log=download">Replay Log</a>
			</li>
		</ul>
	</div>
	<div id="box" style="background-color:#3F2828; margin-left:530px; width:408px; margin-top:0px;">
		<ul>
			<li>
				<?php if($tourney['status'] == 'archived'): ?>
					<a href="/admin/tournaments/add">Start a new Tournament</a>
				<?php elseif($tourney['status'] == 'pending'): ?>
					<a href="/admin/tournaments/edit">Start Group Stage</a>
				<?php elseif($tourney['status'] == 'group'): ?>
					<a href="/admin/tournaments/edit">Start the Playoff</a>
				<?php elseif($tourney['status'] == 'finished'): ?>
					<a href="/admin/tournaments/edit">Close the Tournament</a>
				<?php endif; ?>

                <?php if ($tourney['status'] == Tournament::GROUP || $tourney['status'] == Tournament::PLAYOFF): ?>
                    <?php
                    echo $this->Html->link(
                        'Report tech. win',
                        '/admin/games/add'
                    );
                    ?>
                <?php endif; ?>
			</li>
			<li>
				<a href="/admin/rules/edit">Edit Rules</a>
			</li>
			<?php if($tourney['status'] == 'group'): ?>
				<li>
					<a href="/admin/groups/edit">Replace a Player</a>
				</li>
			<?php endif; ?>
		</ul>
	</div>
<?php else: ?>
	<div id="authMessage" class="message">
		You don't have the required rights to access that page.
	</div>
<?php endif; ?>
