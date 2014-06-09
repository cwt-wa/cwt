<?php if (!$logged_in): ?>
    <div id="box" style="background-color:#29110D; text-align:center;">
        Log in, if you want to start your own Channel.
    </div>
<?php endif; ?>

<?php if ($logged_in && !$up_channel['maintainer']): ?>
    <?php echo $this->Html->script('channel', array('inline' => false)); ?>
    <script type="text/javascript">
        $(document).ready(function () {
            $('#color1').css('border', '1px solid red');
        });
    </script>
    <div id="box" style="background-color:#3F2828; text-align:center;">
        <div id="addChannel" style="text-align:center; width:500px; padding-left:200px;">
            <?php
            echo $this->Form->create('Channel', array(
                'inputDefaults' => array(
                    'div' => false,
                    'style' => 'text-align:center;',
                    'label' => false
                )
            ));
            ?>
            <fieldset>
                <legend><b>Start your own Live Stream Channel</b></legend>
                <br>

                <div id="box" style="border:none; box-shadow:none; padding:0px;">
                    Channel Title<br>
                    <?php echo $this->Form->input('title'); ?>
                    <br><br>
                    Embedding Code<br>
                    <?php echo $this->Form->input('embedcode'); ?>
                    <br><br>
                    Channel Color<br>

                    <div
                        style="background-color:white; width:156px; text-align:center; padding:2px; border:1px solid #7F9DB9; margin-left:158px;">
                        <div id="color1"
                             style="background-color:#2F2923; width:15px; height:15px; cursor:pointer; display:inline-block;">
                            &nbsp;
                        </div>
                        <div id="color2"
                             style="background-color:#887059; width:15px; height:15px; cursor:pointer; display:inline-block;">
                            &nbsp;
                        </div>
                        <div id="color3"
                             style="background-color:#405263; width:15px; height:15px; cursor:pointer; display:inline-block;">
                            &nbsp;
                        </div>
                        <div id="color4"
                             style="background-color:#3E283E; width:15px; height:15px; cursor:pointer; display:inline-block;">
                            &nbsp;
                        </div>
                        <div id="color5"
                             style="background-color:#283E28; width:15px; height:15px; cursor:pointer; display:inline-block;">
                            &nbsp;
                        </div>
                    </div>
                    <?php
                    echo $this->Form->hidden('color', array(
                        'id' => 'ChannelColor',
                        'value' => 'rgb(47, 41, 35)'
                    ));
                    ?>
                </div>
            </fieldset>
            <br>
            <?php echo $this->Form->end('Put your Channel online'); ?>
        </div>
    </div>
<?php endif; ?>
