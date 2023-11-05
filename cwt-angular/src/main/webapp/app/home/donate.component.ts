import {Component, OnInit, Input} from "@angular/core";

@Component({
    selector: 'cwt-donate',
    template: `
  <progress max="170" value="50"></progress>
<div class="progress">
  <div class="progress-bar bg-danger" role="progressbar" style="width: 80%" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100">
    {{ raisedAmount }} of {{ targetAmount }}
  </div>
</div>
`,
})
export class DonateComponent implements OnInit {

    @Input() productId;
    @Input() raisedAmount;
    @Input() targetAmount;

    constructor() {
    }

    public ngOnInit(): void {
        this.url = "https://www.paypal.com/donate?campaign_id=" + this.productId;
    }
}

