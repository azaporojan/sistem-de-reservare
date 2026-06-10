/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type SeatStatusDto = {
    id?: number;
    label?: string;
    row?: number;
    col?: number;
    booked?: boolean;
    status?: SeatStatusDto.status;
    bookedBy?: string;
    reservationId?: string;
};
export namespace SeatStatusDto {
    export enum status {
        FREE = 'FREE',
        PENDING = 'PENDING',
        CONFIRMED = 'CONFIRMED',
    }
}

