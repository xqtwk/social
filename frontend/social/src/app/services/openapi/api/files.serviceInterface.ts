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



export interface FilesServiceInterface {
    defaultHeaders: HttpHeaders;
    configuration: Configuration;

    /**
     * 
     * 
     * @param username 
     * @param filename 
     */
    displayFile(username: string, filename: string, extraHttpRequestParams?: any): Observable<Blob>;

}