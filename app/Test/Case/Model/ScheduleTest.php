<?php

class ScheduleTest extends CakeTestCase
{
    public function setUp()
    {
        parent::setUp();
        $this->Schedule = ClassRegistry::init('Schedule');
    }

    public function testDaysLeft()
    {
        $this->assertEquals(
            [
                '2018-12-29' => 'Dec 29',
                '2018-12-30' => 'Dec 30',
                '2018-12-31' => 'Dec 31',
                '2019-01-01' => 'Jan 1',
                '2019-01-02' => 'Jan 2',
                '2019-01-03' => 'Jan 3',
                '2019-01-04' => 'Jan 4'],
            $this->Schedule->daysLeft(mktime(12, 34, 12, 12, 29, 2018), 6));
    }
}
