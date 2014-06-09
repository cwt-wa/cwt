<?php if (is_array($suggestions)): ?>
    <?php foreach ($suggestions as $suggestion): ?>
        <div class="nick_suggest_item" id="<?php echo $suggestion; ?>" onclick="inputNick(this.id)">
            <?php echo $suggestion; ?>
        </div>
    <?php endforeach; ?>
<?php else: ?>
    <?php echo $suggestions; ?>
<?php endif; ?>
