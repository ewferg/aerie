/**
 * Copyright 2018, by the California Institute of Technology. ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws and regulations.
 * By accepting this document, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons
 */

import { createFeatureSelector, createSelector } from '@ngrx/store';
import { RavenSource, StringTMap } from '../../shared/models';
import { getSortedChildIds } from '../../shared/util';
import { State } from '../raven-store';
import { SourceExplorerState } from '../reducers/source-explorer.reducer';

const treeBySourceId = (state: SourceExplorerState) => state.treeBySourceId;

const featureSelector = createFeatureSelector<State>('raven');
export const getSourceExplorerState = createSelector(
  featureSelector,
  (state: State): SourceExplorerState => state.sourceExplorer,
);

export const getCurrentState = createSelector(
  getSourceExplorerState,
  (state: SourceExplorerState) => state.currentState,
);

export const getCurrentStateId = createSelector(
  getSourceExplorerState,
  (state: SourceExplorerState) => state.currentStateId,
);

export const getCustomFiltersBySourceId = createSelector(
  getSourceExplorerState,
  (state: SourceExplorerState) => state.customFiltersBySourceId,
);

export const getFiltersByTarget = createSelector(
  getSourceExplorerState,
  (state: SourceExplorerState) => state.filtersByTarget,
);

export const getInitialSourcesLoaded = createSelector(
  getSourceExplorerState,
  (state: SourceExplorerState) => state.initialSourcesLoaded,
);

export const getSourceExplorerPending = createSelector(
  getSourceExplorerState,
  (state: SourceExplorerState) => state.fetchPending,
);

export const getPins = createSelector(
  getSourceExplorerState,
  (state: SourceExplorerState) => state.pins,
);

export const getSelectedSourceId = createSelector(
  getSourceExplorerState,
  (state: SourceExplorerState) => state.selectedSourceId,
);

export const getTreeBySourceId = createSelector(
  getSourceExplorerState,
  treeBySourceId,
);

export const treeSortedChildIds = createSelector(
  getTreeBySourceId,
  (tree: StringTMap<RavenSource>) =>
    getSortedChildIds(tree, tree['/'].childIds),
);