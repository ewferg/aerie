/// <reference path="./TemporalPolyfillTypes.ts"; />
import type * as WindowsExpressions from "./constraints-ast.js";
import "./constraints-edsl-fluent-api.js";

export interface ActivityTemplate {
  activityType: string,
  args: {[key: string]: any},
}

export interface ClosedOpenInterval {
  start: Temporal.Duration
  end: Temporal.Duration
}

export type CardinalityGoalArguments =
  |  { duration: Temporal.Duration, occurrence: number }
  |  { duration: Temporal.Duration }
  |  { occurrence: number }

export enum NodeKind {
  ActivityRecurrenceGoal = 'ActivityRecurrenceGoal',
  ActivityCoexistenceGoal = 'ActivityCoexistenceGoal',
  ActivityCardinalityGoal = 'ActivityCardinalityGoal',
  ActivityExpression = 'ActivityExpression',
  GoalAnd = 'GoalAnd',
  GoalOr = 'GoalOr',
  ApplyWhen = 'ApplyWhen'
}

/**
 * Goal
 *
 * Action(Window[]) => void
 *
 * Operates on windows and causes some plan mutation
 */
export type Goal =
  | ActivityRecurrenceGoal
  | ActivityCoexistenceGoal
  | ActivityCardinalityGoal
  ;

export interface ActivityRecurrenceGoal {
  kind: NodeKind.ActivityRecurrenceGoal,
  activityTemplate: ActivityTemplate,
  interval: Temporal.Duration,
}

export interface ActivityCardinalityGoal {
  kind: NodeKind.ActivityCardinalityGoal,
  activityTemplate: ActivityTemplate,
  specification: CardinalityGoalArguments
}

export interface ActivityCoexistenceGoal {
  kind: NodeKind.ActivityCoexistenceGoal,
  activityTemplate: ActivityTemplate,
  forEach: WindowsExpressions.WindowsExpression | ActivityExpression,
  startConstraint: ActivityTimingConstraintSingleton | ActivityTimingConstraintRange | undefined,
  endConstraint: ActivityTimingConstraintSingleton | ActivityTimingConstraintRange | undefined,
}

export interface ActivityExpression {
  kind: NodeKind.ActivityExpression
  type: string
}

export type TimeExpression =
    | TimeExpressionRelativeFixed

export enum TimeExpressionOperator {
  Plus = "TimeExpressionOperatorPlus",
  Minus = "TimeExpressionOperatorMinus"
}

export enum TimeAnchor {
  Start = "Start",
  End = "End"
}

export interface TimeExpressionRelativeFixed {
  anchor: TimeAnchor,
  fixed: boolean  // true means op(anchor, operand) is exactly the time, false means it's a range between op(anchor, operand) and anchor
  operation: {
    operator: TimeExpressionOperator,
    operand: Temporal.Duration
  }
}

export enum ActivityTimeProperty {
  START = 'START',
  END = 'END'
}

export enum WindowProperty {
  START = 'START',
  END = 'END'
}

export enum TimingConstraintOperator {
  PLUS = 'PLUS',
  MINUS = 'MINUS'
}

export interface ActivityTimingConstraintSingleton {
  windowProperty: WindowProperty;
  operator: TimingConstraintOperator;
  operand: Temporal.Duration
  singleton: true
}

export interface ActivityTimingConstraintRange {
  windowProperty: WindowProperty;
  operator: TimingConstraintOperator;
  operand: Temporal.Duration
  singleton: false
}

export type GoalSpecifier =
  | Goal
  | GoalComposition
  | GoalQualification
  ;


/**
 * Goal Composition
 *
 * Compose goals together
 */
export type GoalComposition =
  | GoalAnd
  | GoalOr
  ;

export interface GoalAnd {
  kind: NodeKind.GoalAnd,
  goals: GoalSpecifier[],
}

export interface GoalOr {
  kind: NodeKind.GoalOr,
  goals: GoalSpecifier[],
}

/**
 * Goal Qualification
 *
 * Modify goals
 */
export type GoalQualification = ApplyWhen;

export interface ApplyWhen {
  kind: NodeKind.ApplyWhen,
  goal: GoalSpecifier,
  window: WindowsExpressions.WindowsExpression
}
