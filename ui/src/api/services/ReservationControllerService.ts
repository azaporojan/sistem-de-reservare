/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BookRoomRequest } from '../models/BookRoomRequest';
import type { BookSeatRequest } from '../models/BookSeatRequest';
import type { ReservationResponse } from '../models/ReservationResponse';
import type { ReviewRequest } from '../models/ReviewRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ReservationControllerService {
    /**
     * @returns ReservationResponse OK
     * @throws ApiError
     */
    public static decline({
        id,
        requestBody,
    }: {
        id: string,
        requestBody: ReviewRequest,
    }): CancelablePromise<ReservationResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/reservations/{id}/decline',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns ReservationResponse OK
     * @throws ApiError
     */
    public static confirm({
        id,
        requestBody,
    }: {
        id: string,
        requestBody: ReviewRequest,
    }): CancelablePromise<ReservationResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/reservations/{id}/confirm',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns ReservationResponse Created
     * @throws ApiError
     */
    public static bookSeat({
        requestBody,
    }: {
        requestBody: BookSeatRequest,
    }): CancelablePromise<ReservationResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/reservations/seat',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns ReservationResponse Created
     * @throws ApiError
     */
    public static bookRoom({
        requestBody,
    }: {
        requestBody: BookRoomRequest,
    }): CancelablePromise<ReservationResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/reservations/room',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns ReservationResponse OK
     * @throws ApiError
     */
    public static listAll(): CancelablePromise<Array<ReservationResponse>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations',
        });
    }
    /**
     * @returns ReservationResponse OK
     * @throws ApiError
     */
    public static getById({
        id,
    }: {
        id: string,
    }): CancelablePromise<ReservationResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns ReservationResponse OK
     * @throws ApiError
     */
    public static cancel({
        id,
    }: {
        id: string,
    }): CancelablePromise<ReservationResponse> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/reservations/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns ReservationResponse OK
     * @throws ApiError
     */
    public static listMy(): CancelablePromise<Array<ReservationResponse>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/reservations/my',
        });
    }
}
