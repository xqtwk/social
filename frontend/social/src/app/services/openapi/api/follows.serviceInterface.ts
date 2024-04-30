/**
 * OpenApi specification - Social
 * OpenApi documentation for this cozy social  network project
 *
 * The version of the OpenAPI document: 1.0
 * Contact: dev@gmail.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { HttpHeaders }                                       from '@angular/common/http';

import { Observable }                                        from 'rxjs';

import { UserPublicDto } from '../model/models';


import { Configuration }                                     from '../configuration';



export interface FollowsServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * Get users that user follows
     * 
     * @param username 
     */
    getFollowedUsers(username: string, extraHttpRequestParams?: any): Observable<Array<UserPublicDto>>;

    /**
     * Get user followers
     * 
     * @param username 
     */
    getUserFollowers(username: string, extraHttpRequestParams?: any): Observable<Array<UserPublicDto>>;

    /**
     * Toggle follow
     * 
     * @param followedUsername 
     */
    toggleFollow(followedUsername: string, extraHttpRequestParams?: any): Observable<string>;

}