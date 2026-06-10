/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CreateUserRequest } from '../models/CreateUserRequest';
import type { GrantPermissionRequest } from '../models/GrantPermissionRequest';
import type { UpdateProfileRequest } from '../models/UpdateProfileRequest';
import type { UpdateUserRequest } from '../models/UpdateUserRequest';
import type { UserResponse } from '../models/UserResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class UserControllerService {
    /**
     * @returns UserResponse OK
     * @throws ApiError
     */
    public static listUsers(): CancelablePromise<Array<UserResponse>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/users',
        });
    }
    /**
     * @returns UserResponse Created
     * @throws ApiError
     */
    public static createUser({
        requestBody,
    }: {
        requestBody: CreateUserRequest,
    }): CancelablePromise<UserResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/users',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns void
     * @throws ApiError
     */
    public static grantPermission({
        id,
        requestBody,
    }: {
        id: string,
        requestBody: GrantPermissionRequest,
    }): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/users/{id}/permissions',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns void
     * @throws ApiError
     */
    public static revokePermission({
        id,
        requestBody,
    }: {
        id: string,
        requestBody: GrantPermissionRequest,
    }): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/users/{id}/permissions',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns UserResponse OK
     * @throws ApiError
     */
    public static getUser({
        id,
    }: {
        id: string,
    }): CancelablePromise<UserResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/users/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns void
     * @throws ApiError
     */
    public static deleteUser({
        id,
    }: {
        id: string,
    }): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/users/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns UserResponse OK
     * @throws ApiError
     */
    public static updateUser({
        id,
        requestBody,
    }: {
        id: string,
        requestBody: UpdateUserRequest,
    }): CancelablePromise<UserResponse> {
        return __request(OpenAPI, {
            method: 'PATCH',
            url: '/api/users/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns UserResponse OK
     * @throws ApiError
     */
    public static getMe(): CancelablePromise<UserResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/users/me',
        });
    }
    /**
     * @returns UserResponse OK
     * @throws ApiError
     */
    public static updateMe({
        requestBody,
    }: {
        requestBody: UpdateProfileRequest,
    }): CancelablePromise<UserResponse> {
        return __request(OpenAPI, {
            method: 'PATCH',
            url: '/api/users/me',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
