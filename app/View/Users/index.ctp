<?php
$options = array(
    'update' => '#content',
    'evalScripts' => true
);
$this->Paginator->options($options);
?>

<div id="box" style="background-color:#161C1D; text-align:center;">
    <h1>
        Users
    </h1>
</div>
<?php if (!$logged_in): ?>
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
                <?php echo $this->Paginator->sort('trophies') ?>
            </td>
            <td style="border-bottom:1px solid #2E2E2E; font-size:12pt;" align="center" width="120">
                <b>Contact</b>
            </td>
        </tr>

        <?php foreach ($users as $user): ?>
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
                        '/users/view/' . $user['User']['id']);
                    ?>
                </td>
                <td style="border-bottom:1px solid #2E2E2E" align="center">
                    <?php echo $user['User']['participations']; ?>
                </td>
                <td style="border-bottom:1px solid #2E2E2E" align="center">
                    <?php
                    foreach ($achievements as $key => $val) {
                        if ($val['gold'] == $user['User']['id']) {
                            echo $this->Html->image('medals/' . array_search($val['gold'], $val) . '.gif', array(
                                        'alt' => $key,
                                        'title' => $key,
                                        'style' => 'height:15px; width:auto;'
                                    )
                                ) . '&nbsp;';
                        } elseif ($val['silver'] == $user['User']['id']) {
                            echo $this->Html->image('medals/' . array_search($val['silver'], $val) . '.gif', array(
                                        'alt' => $key,
                                        'title' => $key,
                                        'style' => 'height:15px; width:auto;'
                                    )
                                ) . '&nbsp;';
                        } elseif ($val['bronze'] == $user['User']['id']) {
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
                    <?php if ($user['Profile']['skype']): ?>
                        <?php
                        echo $this->Html->link(
                            $this->Html->image('contact/skype.jpg', array(
                                'height' => 14,
                                'width' => 14
                            )),
                            'http://myskype.info/' . $user['Profile']['skype'], array(
                                'alt' => $user['Profile']['skype'],
                                'title' => $user['Profile']['skype'],
                                'escape' => false,
                                'target' => '_blank'
                            )
                        )
                        ?>
                    <?php endif; ?>
                    <?php if ($user['Profile']['icq']): ?>
                        <?php
                        echo $this->Html->link(
                            $this->Html->image('contact/icq.png', array(
                                'height' => 14,
                                'width' => 14
                            )),
                            'http://icq.com/people/' . $user['Profile']['icq'], array(
                                'alt' => $user['Profile']['icq'],
                                'title' => $user['Profile']['icq'],
                                'escape' => false,
                                'target' => '_blank'
                            )
                        )
                        ?>
                    <?php endif; ?>
                    <?php if ($user['Profile']['email']): ?>
                        <?php
                        echo $this->Html->link(
                            $this->Html->image('contact/email.png', array(
                                'height' => 14,
                                'width' => 14
                            )),
                            'mailto:' . $user['Profile']['email'], array(
                                'alt' => $user['Profile']['email'],
                                'title' => $user['Profile']['email'],
                                'escape' => false
                            )
                        )
                        ?>
                    <?php endif; ?>
                    <?php if ($user['Profile']['facebook']): ?>
                        <?php
                        echo $this->Html->link(
                            $this->Html->image('contact/facebook.png', array(
                                'height' => 14,
                                'width' => 14
                            )),
                            $user['Profile']['facebook'], array(
                                'alt' => $user['Profile']['facebook'],
                                'title' => $user['Profile']['facebook'],
                                'escape' => false,
                                'target' => '_blank'
                            )
                        )
                        ?>
                    <?php endif; ?>
                    <?php if ($user['Profile']['googlep']): ?>
                        <?php
                        echo $this->Html->link(
                            $this->Html->image('contact/googlep.png', array(
                                'height' => 14,
                                'width' => 14
                            )),
                            $user['Profile']['googlep'], array(
                                'alt' => $user['Profile']['googlep'],
                                'title' => $user['Profile']['googlep'],
                                'escape' => false,
                                'target' => '_blank'
                            )
                        )
                        ?>
                    <?php endif; ?>
                    <?php if ($user['Profile']['twitter']): ?>
                        <?php
                        echo $this->Html->link(
                            $this->Html->image('contact/twitter.png', array(
                                'height' => 14,
                                'width' => 14
                            )),
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
        echo $this->Paginator->numbers(array('separator' => '', 'modulus' => 24));
        echo $this->Paginator->next(__('next') . ' >', array(), null, array('class' => 'next disabled'));
        ?>
    </div>
</div>

<?php echo $this->Js->writeBuffer(); // Write cached scripts ?>
