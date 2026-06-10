/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AvailabilityRequest } from '../models/AvailabilityRequest';
import type { AvailabilityResponse } from '../models/AvailabilityResponse';
import type { CreateRoomRequest } from '../models/CreateRoomRequest';
import type { RoomResponse } from '../models/RoomResponse';
import type { SeatMapResponse } from '../models/SeatMapResponse';
import type { UpdateRoomRequest } from '../models/UpdateRoomRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class RoomControllerService {
    /**
     * @returns RoomResponse OK
     * @throws ApiError
     */
    public static listRooms(): CancelablePromise<Array<RoomResponse>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/rooms',
        });
    }
    /**
     * @returns RoomResponse Created
     * @throws ApiError
     */
    public static createRoom({
        requestBody,
    }: {
        requestBody: CreateRoomRequest,
    }): CancelablePromise<RoomResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/rooms',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns RoomResponse OK
     * @throws ApiError
     */
    public static getRoom({
        id,
    }: {
        id: number,
    }): CancelablePromise<RoomResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/rooms/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns void
     * @throws ApiError
     */
    public static deleteRoom({
        id,
    }: {
        id: number,
    }): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/rooms/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * @returns RoomResponse OK
     * @throws ApiError
     */
    public static updateRoom({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: UpdateRoomRequest,
    }): CancelablePromise<RoomResponse> {
        return __request(OpenAPI, {
            method: 'PATCH',
            url: '/api/rooms/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns SeatMapResponse OK
     * @throws ApiError
     */
    public static seatMap({
        id,
        start,
        end,
    }: {
        id: number,
        start?: string,
        end?: string,
    }): CancelablePromise<SeatMapResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/rooms/{id}/seats',
            path: {
                'id': id,
            },
            query: {
                'start': start,
                'end': end,
            },
        });
    }
    /**
     * @returns AvailabilityResponse OK
     * @throws ApiError
     */
    public static availability({
        id,
        requestBody,
    }: {
        id: number,
        requestBody: AvailabilityRequest,
    }): CancelablePromise<AvailabilityResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/rooms/{id}/availability',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
