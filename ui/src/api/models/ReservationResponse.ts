/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type ReservationResponse = {
    id?: string;
    userId?: string;
    roomId?: number;
    roomName?: string;
    seatId?: number;
    seatLabel?: string;
    type?: ReservationResponse.type;
    startDateTime?: string;
    endDateTime?: string;
    seatsRequested?: number;
    status?: ReservationResponse.status;
    notes?: string;
    reviewedBy?: string;
    reviewNote?: string;
    createdAt?: string;
};
export namespace ReservationResponse {
    export enum type {
        ROOM = 'ROOM',
        SEAT = 'SEAT',
    }
    export enum status {
        PENDING = 'PENDING',
        CONFIRMED = 'CONFIRMED',
        COMPLETED = 'COMPLETED',
        DECLINED = 'DECLINED',
        CANCELLED = 'CANCELLED',
    }
}

