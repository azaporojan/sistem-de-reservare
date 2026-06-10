/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { SeatStatusDto } from './SeatStatusDto';
export type SeatMapResponse = {
    roomId?: number;
    roomName?: string;
    rows?: number;
    cols?: number;
    roomBooked?: boolean;
    roomBookedBy?: string;
    seats?: Array<SeatStatusDto>;
};

