/**
 * Copyright 2018, by the California Institute of Technology. ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws and regulations.
 * By accepting this document, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons
 */

import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';

import { PolymerModule } from '@codebakery/origami';
import { SortablejsModule } from 'angular-sortablejs';
import { AngularSplitModule } from 'angular-split';

import { EffectsModule } from '@ngrx/effects';
import { RouterStateSerializer, StoreRouterConnectingModule } from '@ngrx/router-store';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';

import { CustomRouterStateSerializer, routes } from './routes';
import { metaReducers, reducers } from './store';

import { environment } from './../environments/environment';

import { MaterialModule } from './shared/material';

import { ColorPickerModule } from 'ngx-color-picker';

import {
  AppComponent,
  SourceExplorerComponent,
  TimelineComponent,
} from './containers';

import {
  RavenBandsComponent,
  RavenConfirmDialogComponent,
  RavenEpochsComponent,
  RavenGlobalSettingsComponent,
  RavenSettingsComponent,
  RavenStateSaveDialogComponent,
  RavenTreeComponent,
} from './components';

import {
  DisplayEffects,
  EpochsEffects,
  RouterEffects,
  SourceExplorerEffects,
} from './effects';

import {
  TimelineGuard,
} from './guards';

import {
  HasKeysPipe,
  KeyByPipe,
} from './pipes';

export const DECLARATIONS = [
  AppComponent,
  HasKeysPipe,
  KeyByPipe,
  RavenBandsComponent,
  RavenConfirmDialogComponent,
  RavenEpochsComponent,
  RavenGlobalSettingsComponent,
  RavenSettingsComponent,
  RavenStateSaveDialogComponent,
  RavenTreeComponent,
  SourceExplorerComponent,
  TimelineComponent,
];

export const ENTRY_COMPONENTS = [
  RavenConfirmDialogComponent,
  RavenStateSaveDialogComponent,
];

export const EFFECTS = [
  DisplayEffects,
  EpochsEffects,
  RouterEffects,
  SourceExplorerEffects,
];

export const MODULES = [
  CommonModule,
  BrowserModule,
  BrowserAnimationsModule,
  HttpClientModule,
  FormsModule,
  MaterialModule,
  AngularSplitModule,
  FlexLayoutModule,
  PolymerModule.forRoot(),
  SortablejsModule.forRoot({}),
  RouterModule.forRoot(routes, { useHash: true }),
  StoreModule.forRoot(reducers, { metaReducers }),
  StoreRouterConnectingModule,
  !environment.production ? StoreDevtoolsModule.instrument({ maxAge: 10 }) : [],
  EffectsModule.forRoot(EFFECTS),
  ColorPickerModule,
];

export const PROVIDERS = [
  {
    provide: RouterStateSerializer,
    useClass: CustomRouterStateSerializer,
  },
  TimelineGuard,
];

export const SCHEMAS = [
  CUSTOM_ELEMENTS_SCHEMA,
];

@NgModule({
  bootstrap: [
    AppComponent,
  ],
  declarations: DECLARATIONS,
  entryComponents: ENTRY_COMPONENTS,
  exports: DECLARATIONS,
  imports: MODULES,
  providers: PROVIDERS,
  schemas: SCHEMAS,
})
export class AppModule {}
