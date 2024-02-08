import { Routes } from '@angular/router';
import {HomeComponent} from "./pages/home/home.component";
import {ProfileComponent} from "./pages/profile/profile.component";
import {GroupsComponent} from "./pages/groups/groups.component";
import {MessagesComponent} from "./pages/messages/messages.component";
import {FriendsComponent} from "./pages/friends/friends.component";

export const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'home', component: HomeComponent},
  {path: 'profile', component: ProfileComponent},
  {path: 'groups', component: GroupsComponent},
  {path: 'messages', component: MessagesComponent},
  {path: 'friends', component: FriendsComponent}
];
