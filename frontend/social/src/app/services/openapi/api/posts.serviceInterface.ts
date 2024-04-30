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

import { Post } from '../model/models';
import { PostRequest } from '../model/models';
import { UserPublicDto } from '../model/models';


import { Configuration }                                     from '../configuration';



export interface PostsServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * Create new post
     * 
     * @param postRequest 
     * @param photos 
     */
    createPost(postRequest: PostRequest, photos: Array<Blob>, extraHttpRequestParams?: any): Observable<Post>;

    /**
     * Delete post
     * 
     * @param postId 
     */
    deletePost(postId: number, extraHttpRequestParams?: any): Observable<object>;

    /**
     * Edit post
     * 
     * @param postId 
     * @param newContent 
     * @param photos 
     */
    editPost(postId: number, newContent: string, photos: Array<Blob>, extraHttpRequestParams?: any): Observable<Post>;

    /**
     * Get user\&#39;s liked posts
     * 
     */
    getLikedPosts(extraHttpRequestParams?: any): Observable<Array<Post>>;

    /**
     * Get news feed for User
     * 
     */
    getNewsFeed(extraHttpRequestParams?: any): Observable<Array<Post>>;

    /**
     * Get post
     * 
     * @param postId 
     */
    getPost(postId: number, extraHttpRequestParams?: any): Observable<Post>;

    /**
     * Get user\&#39;s posts
     * 
     * @param username 
     */
    getPostsByUser(username: string, extraHttpRequestParams?: any): Observable<Array<Post>>;

    /**
     * Get post\&#39;s like list(users who liked posts)
     * 
     * @param postId 
     */
    getUsersWhoLikedPost(postId: number, extraHttpRequestParams?: any): Observable<Array<UserPublicDto>>;

}
