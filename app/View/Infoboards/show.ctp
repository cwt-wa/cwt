<?php foreach ($messages as $msg): ?>
    <div id="post">
        <?php echo $this->Time->format('M j, H:i', $msg['Infoboard']['created']); ?>

        <?php if ($msg['Infoboard']['user_id'] == AuthComponent::user('id')): ?>
            <b><?php echo $this->Html->link($msg['User']['username'],
                    array('controller' => 'users', 'action' => 'view', $msg['User']['id'])); ?></b>:
        <?php elseif ($msg['Infoboard']['category'] == 4): ?>
            <?php echo $msg['Infoboard']['guest'] ?>:
        <?php
        elseif ($msg['Infoboard']['category'] == 3): ?>

        <?php
        else: ?>
            <?php echo $this->Html->link($msg['User']['username'],
                array('controller' => 'users', 'action' => 'view', $msg['User']['id'])); ?>:
        <?php endif; ?>


        <?php if ($msg['Infoboard']['category'] == 2): ?>
            <font
                color="red"><?php echo $this->Text->autoLink($msg['Infoboard']['message']); ?></font>
        <?php elseif ($msg['Infoboard']['category'] == 3): ?>
            <i><?php echo html_entity_decode($msg['Infoboard']['message']); ?></i>
        <?php
        else: ?>
            <?php echo $this->Text->autoLink($msg['Infoboard']['message']); ?>
        <?php endif; ?>
    </div>
<?php endforeach; ?>
