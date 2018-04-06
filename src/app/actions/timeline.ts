/**
 * Copyright 2018, by the California Institute of Technology. ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws and regulations.
 * By accepting this document, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons
 */

import { Action } from '@ngrx/store';

import {
  BaseType,
  RavenCompositeBand,
  RavenSortMessage,
  RavenSubBand,
  RavenTimeRange,
  StringTMap,
} from './../shared/models';

// Action Types.
export enum TimelineActionTypes {
  AddBand                      = '[timeline] add_band',
  AddPointsToSubBand           = '[timeline] add_points_to_sub_band',
  AddSubBand                   = '[timeline] add_sub_band',
  ChangeCurrentTimeCursor      = '[timeline] change_current_time_cursor',
  ChangeDateFormat             = '[timeline] change_date_format',
  ChangeTooltip                = '[timeline] change_tooltip',
  ChangeLabelFontSize          = '[timeline] change_label_fonr_size',
  ChangeLabelFontStyle         = '[timeline] change_label_font_style',
  ChangeLabelWidth             = '[timeline] change_label_width',
  ChangeResourceColor          = '[timeline] change_resource_color',
  RemoveBandsOrPointsForSource = '[timeline] remove_bands_or_points_for_source',
  RemoveSubBand                = '[timeline] remove_sub_band',
  SelectBand                   = '[timeline] select_band',
  SortBands                    = '[timeline] sort_bands',
  UpdateBand                   = '[timeline] update_band',
  UpdateSubBand                = '[timeline] update_sub_band',
  UpdateTimeline               = '[timeline] update_timeline',
  UpdateViewTimeRange          = '[timeline] update_view_time_range',
}

// Actions.
export class AddBand implements Action {
  readonly type = TimelineActionTypes.AddBand;

  constructor(
    public sourceId: string,
    public band: RavenCompositeBand,
  ) {}
}

export class AddPointsToSubBand implements Action {
  readonly type = TimelineActionTypes.AddPointsToSubBand;

  constructor(
    public sourceId: string,
    public bandId: string,
    public subBandId: string,
    public points: any[],
  ) {}
}

export class AddSubBand implements Action {
  readonly type = TimelineActionTypes.AddSubBand;

  constructor(
    public sourceId: string,
    public bandId: string,
    public subBand: RavenSubBand,
  ) {}
}

export class RemoveBandsOrPointsForSource implements Action {
  readonly type = TimelineActionTypes.RemoveBandsOrPointsForSource;

  constructor(public sourceId: string) {}
}

export class RemoveSubBand implements Action {
  readonly type = TimelineActionTypes.RemoveSubBand;

  constructor(public subBandId: string) {}
}

export class SelectBand implements Action {
  readonly type = TimelineActionTypes.SelectBand;

  constructor(public bandId: string) {}
}

export class SortBands implements Action {
  readonly type = TimelineActionTypes.SortBands;

  constructor(public sort: StringTMap<RavenSortMessage>) {}
}

export class UpdateBand implements Action {
  readonly type = TimelineActionTypes.UpdateBand;

  constructor(public bandId: string, public update: StringTMap<BaseType>) {}
}

export class UpdateSubBand implements Action {
  readonly type = TimelineActionTypes.UpdateSubBand;

  constructor(public bandId: string, public subBandId: string, public update: StringTMap<BaseType>) {}
}

export class UpdateTimeline implements Action {
  readonly type = TimelineActionTypes.UpdateTimeline;

  constructor(public update: StringTMap<BaseType>) {}
}

export class UpdateViewTimeRange implements Action {
  readonly type = TimelineActionTypes.UpdateViewTimeRange;

  constructor(public viewTimeRange: RavenTimeRange) {}
}

export class ChangeDateFormat implements Action {
  readonly type = TimelineActionTypes.ChangeDateFormat;

  constructor(public dateFormat: string) {}
}

export class ChangeCurrentTimeCursor implements Action {
  readonly type = TimelineActionTypes.ChangeCurrentTimeCursor;

  constructor(public currentTimeCursor: boolean) {}
}

export class ChangeTooltip implements Action {
  readonly type = TimelineActionTypes.ChangeTooltip;

  constructor(public tooltip: boolean) {}
}

export class ChangeLabelFontSize implements Action {
  readonly type = TimelineActionTypes.ChangeLabelFontSize;

  constructor(public labelFontSize: number) {}
}

export class ChangeLabelFontStyle implements Action {
  readonly type = TimelineActionTypes.ChangeLabelFontStyle;

  constructor(public labelFontStyle: string) {}
}

export class ChangeLabelWidth implements Action {
  readonly type = TimelineActionTypes.ChangeLabelWidth;

  constructor(public labelWidth: number) {}
}

export class ChangeResourceColor implements Action {
  readonly type = TimelineActionTypes.ChangeResourceColor;

  constructor(public resourceColor: string) {}
}

// Union type of all actions.
export type TimelineAction =
  AddBand |
  AddPointsToSubBand |
  AddSubBand |
  ChangeCurrentTimeCursor |
  ChangeDateFormat |
  ChangeTooltip |
  ChangeLabelFontSize |
  ChangeLabelFontStyle |
  ChangeLabelWidth |
  ChangeResourceColor |
  RemoveBandsOrPointsForSource |
  RemoveSubBand |
  SelectBand |
  SortBands |
  UpdateBand |
  UpdateSubBand |
  UpdateTimeline |
  UpdateViewTimeRange;
