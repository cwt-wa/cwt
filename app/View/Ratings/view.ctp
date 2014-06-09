<div id="boxFloat" class="R"
     style="margin-top:10px; margin-right:5px; width:90px; background-color:#3F3829; padding:15px 10px; height:100px">
    <div style="float:left; margin-right:10px">
        <div style="height:<?php echo $rating['p']['likes'] ?>px; width:5px; background-color:green"></div>
        <div style="height:<?php echo $rating['p']['dislikes'] ?>px; width:5px; background-color:red"></div>
    </div>
    <br><?php echo $rating['likes'] ?> Likes,<br><?php echo $rating['dislikes'] ?> Dislikes<br><br><br>
    <?php if ($logged_in): ?>
        <div id="dblButton">
            <?php if ($rating['trace']['likes'] || $rating['trace']['dislikes']): ?>
                <?php if ($rating['trace']['likes']): ?>
                    <div class="dblButtonTrue" style="background-color:green;">
                        L
                    </div>
                    <div class="dblButtonFalse">
                        D
                    </div>
                <?php else: ?>
                    <div class="dblButtonFalse">
                        L
                    </div>
                    <div class="dblButtonTrue" style="background-color:red;">
                        D
                    </div>
                <?php endif; ?>
            <?php else: ?>
                <div onClick="rate('like', '<?php echo $gameId ?>')" class="dblButtonBasic"
                     style="background-color:green;">
                    L
                </div>
                <div onClick="rate('dislike', '<?php echo $gameId ?>')" class="dblButtonBasic"
                     style="background-color:red;">
                    D
                </div>
            <?php endif; ?>
        </div>
    <?php endif; ?>
</div>
<div id="boxFloat" class="C"
     style="margin-top:10px; width:90px; background-color:#3F3829; padding:15px 10px; height:100px">
    <div style="float:left; margin-right:10px">
        <div style="height:<?php echo $rating['p']['lightside'] ?>px; width:5px; background-color:white"></div>
        <div style="height:<?php echo $rating['p']['darkside'] ?>px; width:5px; background-color:black"></div>
    </div>
    <br><?php echo $rating['lightside'] ?> Lightside,<br><?php echo $rating['darkside'] ?> Darkside<br><br><br>
    <?php if ($logged_in): ?>
        <div id="dblButton">
            <?php if ($rating['trace']['lightside'] || $rating['trace']['darkside']): ?>
                <?php if ($rating['trace']['lightside']): ?>
                    <div class="dblButtonTrue" style="background-color:white; color:black;">
                        L
                    </div>
                    <div class="dblButtonFalse">
                        D
                    </div>
                <?php else: ?>
                    <div class="dblButtonFalse">
                        L
                    </div>
                    <div class="dblButtonTrue" style="background-color:black;">
                        D
                    </div>
                <?php endif; ?>
            <?php else: ?>
                <div onClick="rate('lightside', '<?php echo $gameId ?>')" class="dblButtonBasic"
                     style="background-color:white; color:black;">
                    L
                </div>
                <div onClick="rate('darkside', '<?php echo $gameId ?>')" class="dblButtonBasic"
                     style="background-color:black;">
                    D
                </div>
            <?php endif; ?>
        </div>
    <?php endif; ?>
</div>

