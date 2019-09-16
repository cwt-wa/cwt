import {Injectable} from "@angular/core";

@Injectable()
export class Toastr {

    private toastr = require('toastr/toastr.js');

    error(message: string) {
        return this.toastr.error(message)
    }

    success(message: string) {
        return this.toastr.success(message)
    }

    info(message: string) {
        return this.toastr.info(message)
    }
}
