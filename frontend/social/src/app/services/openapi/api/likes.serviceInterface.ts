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



import { Configuration }                                     from '../configuration';



export interface LikesServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * Toggle like
     * 
     * @param postId 
     */
    toggleLike(postId: number, extraHttpRequestParams?: any): Observable<string>;

}