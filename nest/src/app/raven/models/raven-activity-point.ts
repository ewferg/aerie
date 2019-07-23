/**
 * Copyright 2018, by the California Institute of Technology. ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws and regulations.
 * By accepting this document, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons
 */

import {
  MpsServerActivityPointMetadata,
  MpsServerActivityPointParameter,
} from './index';

export interface RavenActivityPoint {
  activityId: string;
  activityName: string;
  activityParameters: MpsServerActivityPointParameter[];
  activityType: string;
  ancestors: string[];
  arguments: number;
  childrenUrl: string;
  color: string;
  descendantsUrl: string;
  duration: number;
  editable: boolean;
  end: number;
  endTimestamp: string;
  expandedFromPointId: string | null;
  expansion: string;
  hidden: boolean;
  id: string;
  keywordLine: string;
  legend: string;
  message: string | null;
  metadata: MpsServerActivityPointMetadata[];
  pointStatus: string;
  selected: boolean;
  sourceId: string;
  start: number;
  startTimestamp: string;
  subBandId: string;
  type: string;
  uniqueId: string;
}
