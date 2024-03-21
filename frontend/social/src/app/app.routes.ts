import { Routes } from '@angular/router';
import {HomeComponent} from "./components/home/home.component";
import {ProfileComponent} from "./components/profile/profile.component";
import {GroupsComponent} from "./components/groups/groups.component";
import {MessagesComponent} from "./components/messages/messages.component";
import {FriendsComponent} from "./components/friends/friends.component";

export const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'home', component: HomeComponent},
  {path: 'profile', component: ProfileComponent},
  {path: 'groups', component: GroupsComponent},
  {path: 'messages', component: MessagesComponent},
  {path: 'friends', component: FriendsComponent}
];
