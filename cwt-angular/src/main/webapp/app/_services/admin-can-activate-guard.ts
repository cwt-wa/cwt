import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../_services/auth.service';
import { JwtUser } from "../custom";

@Injectable()
export class AdminCanActivateGuard implements CanActivate {

  constructor(private authService: AuthService) {
  }

  canActivate(
      _next: ActivatedRouteSnapshot,
      _state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.authService.authState.then((user: JwtUser | null)  => !!user?.roles?.includes("ROLE_ADMIN"));
  }
}
