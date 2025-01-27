/** START Preface */

export enum TimingTypes {
  ABSOLUTE = 'ABSOLUTE',
  COMMAND_RELATIVE = 'COMMAND_RELATIVE',
  EPOCH_RELATIVE = 'EPOCH_RELATIVE',
  COMMAND_COMPLETE = 'COMMAND_COMPLETE',
}

type SeqJsonTimeType =
  | {
      type: TimingTypes.ABSOLUTE;
      tag: DOY_STRING;
    }
  | {
      type: TimingTypes.COMMAND_RELATIVE;
      tag: HMS_STRING;
    }
  | {
      type: TimingTypes.EPOCH_RELATIVE;
      tag: HMS_STRING;
    }
  | {
      type: TimingTypes.COMMAND_COMPLETE;
    };

export type CommandOptions<
  A extends ArgType[] | { [argName: string]: any } = [] | {},
  M extends Record<string, any> = Record<string, any>,
> = { stem: string; arguments: A; metadata?: M } & (
  | {
      absoluteTime: Temporal.Instant;
    }
  | {
      epochTime: Temporal.Duration;
    }
  | {
      relativeTime: Temporal.Duration;
    }
  // CommandComplete
  | {}
);

export interface CommandSeqJson<A extends ArgType[] = ArgType[]> {
  args: A;
  stem: string;
  time: SeqJsonTimeType;
  type: 'command';
  metadata: Record<string, unknown>;
}

export type ArgType = boolean | string | number;
export type Arrayable<T> = T | Arrayable<T>[];

export interface SequenceSeqJson {
  id: string;
  metadata: Record<string, any>;
  steps: CommandSeqJson[];
}

declare global {
  class Command<
    A extends ArgType[] | { [argName: string]: any } = [] | {},
    M extends Record<string, any> = Record<string, any>,
  > {
    public static new<A extends any[] | { [argName: string]: any }>(opts: CommandOptions<A>): Command<A>;

    public toSeqJson(): CommandSeqJson;
  }

  class Sequence {
    public readonly seqId: string;
    public readonly metadata: Record<string, any>;
    public readonly commands: Command[];

    public static new(opts: { seqId: string; metadata: Record<string, any>; commands: Command[] }): Sequence;

    public toSeqJson(): SequenceSeqJson;
  }

  type Context = {};
  type ExpansionReturn = Arrayable<Command>;

  type U<BitLength extends 8 | 16 | 32 | 64> = number;
  type U8 = U<8>;
  type U16 = U<16>;
  type U32 = U<32>;
  type U64 = U<64>;
  type I<BitLength extends 8 | 16 | 32 | 64> = number;
  type I8 = I<8>;
  type I16 = I<16>;
  type I32 = I<32>;
  type I64 = I<64>;
  type VarString<PrefixBitLength extends number, MaxBitLength extends number> = string;
  type F<BitLength extends 32 | 64> = number;
  type F32 = F<32>;
  type F64 = F<64>;

  // @ts-ignore : 'Commands' found in generated code
  function A(...args: [TemplateStringsArray, ...string[]]): typeof Commands;
  // @ts-ignore : 'Commands' found in generated code
  function A(absoluteTime: Temporal.Instant): typeof Commands;
  // @ts-ignore : 'Commands' found in generated code
  function A(timeDOYString: string): typeof Commands;

  // @ts-ignore : 'Commands' found in generated code
  function R(...args: [TemplateStringsArray, ...string[]]): typeof Commands;
  // @ts-ignore : 'Commands' found in generated code
  function R(duration: Temporal.Duration): typeof Commands;
  // @ts-ignore : 'Commands' found in generated code
  function R(timeHMSString: string): typeof Commands;

  // @ts-ignore : 'Commands' found in generated code
  function E(...args: [TemplateStringsArray, ...string[]]): typeof Commands;
  // @ts-ignore : 'Commands' found in generated code
  function E(duration: Temporal.Duration): typeof Commands;
  // @ts-ignore : 'Commands' found in generated code
  function E(timeHMSString: string): typeof Commands;

  // @ts-ignore : 'Commands' found in generated code
  const C: typeof Commands;
}

export class Command<
  A extends ArgType[] | { [argName: string]: any } = [] | {},
  M extends Record<string, any> = Record<string, any>,
> {
  public readonly stem: string;
  public readonly metadata: M;
  public readonly arguments: A;
  public readonly absoluteTime: Temporal.Instant | null = null;
  public readonly epochTime: Temporal.Duration | null = null;
  public readonly relativeTime: Temporal.Duration | null = null;

  private constructor(opts: CommandOptions<A, M>) {
    this.stem = opts.stem;
    this.arguments = opts.arguments;
    this.metadata = opts.metadata ?? ({} as M);
    if ('absoluteTime' in opts) {
      this.absoluteTime = opts.absoluteTime;
    } else if ('epochTime' in opts) {
      this.epochTime = opts.epochTime;
    } else if ('relativeTime' in opts) {
      this.relativeTime = opts.relativeTime;
    }
  }

  public static new<A extends any[] | { [argName: string]: any }>(opts: CommandOptions<A>): Command<A> {
    if ('absoluteTime' in opts) {
      return new Command<A>({
        ...opts,
        absoluteTime: opts.absoluteTime,
      });
    } else if ('epochTime' in opts) {
      return new Command<A>({
        ...opts,
        epochTime: opts.epochTime,
      });
    } else if ('relativeTime' in opts) {
      return new Command<A>({
        ...opts,
        relativeTime: opts.relativeTime,
      });
    } else {
      return new Command<A>(opts);
    }
  }

  public toSeqJson(): CommandSeqJson {
    return {
      args: typeof this.arguments == 'object' ? Object.values(this.arguments) : this.arguments,
      stem: this.stem,
      time:
        this.absoluteTime !== null
          ? { type: TimingTypes.ABSOLUTE, tag: instantToDoy(this.absoluteTime) }
          : this.epochTime !== null
          ? { type: TimingTypes.EPOCH_RELATIVE, tag: durationToHms(this.epochTime) }
          : this.relativeTime !== null
          ? { type: TimingTypes.COMMAND_RELATIVE, tag: durationToHms(this.relativeTime) }
          : { type: TimingTypes.COMMAND_COMPLETE },
      type: 'command',
      metadata: this.metadata,
    };
  }

  public static fromSeqJson<A extends ArgType[]>(json: CommandSeqJson<A>): Command<A> {
    const timeValue =
      json.time.type === TimingTypes.ABSOLUTE
        ? { absoluteTime: doyToInstant(json.time.tag) }
        : json.time.type === TimingTypes.COMMAND_RELATIVE
        ? { relativeTime: hmsToDuration(json.time.tag) }
        : json.time.type === TimingTypes.EPOCH_RELATIVE
        ? { epochTime: hmsToDuration(json.time.tag) }
        : {};

    return Command.new<A>({
      stem: json.stem,
      arguments: json.args as A,
      metadata: json.metadata,
      ...timeValue,
    });
  }

  public absoluteTiming(absoluteTime: Temporal.Instant): Command<A> {
    return Command.new({
      stem: this.stem,
      arguments: this.arguments,
      absoluteTime: absoluteTime,
    });
  }

  public epochTiming(epochTime: Temporal.Duration): Command<A> {
    return Command.new({
      stem: this.stem,
      arguments: this.arguments,
      epochTime: epochTime,
    });
  }

  public relativeTiming(relativeTime: Temporal.Duration): Command<A> {
    return Command.new({
      stem: this.stem,
      arguments: this.arguments,
      relativeTime: relativeTime,
    });
  }

  public toEDSLString(): string {
    const timeString = this.absoluteTime
      ? `A\`${instantToDoy(this.absoluteTime)}\``
      : this.epochTime
      ? `E\`${durationToHms(this.epochTime)}\``
      : this.relativeTime
      ? `R\`${durationToHms(this.relativeTime)}\``
      : 'C';

    const argsString = Object.keys(this.arguments).length === 0 ? '' : `(${Command.argumentsToString(this.arguments)})`;

    return `${timeString}.${this.stem}${argsString}`;
  }

  private static argumentsToString<A extends ArgType[] | { [argName: string]: any } = [] | {}>(args: A): string {
    if (Array.isArray(args)) {
      const argStrings = args.map((arg) => {
        if (typeof arg === 'string') {
          return `'${arg}'`;
        }
        return arg.toString();
      });

      return argStrings.join(', ');
    }
    else {
      const argStrings = Object.keys(args).reduce((accum, key) => {
        if (typeof args[key] === 'string') {
          accum.push(`${key}: '${args[key]}'`);
        }
        else {
          accum.push(`${key}: ${args[key]}`);
        }
        return accum;
      }, [] as string[]);
      return '{\n' + indent(argStrings.map(argString => argString + ',').join('\n')) + '\n}';
    }
  }
}

export interface SequenceOptions {
  seqId: string;
  metadata: Record<string, any>;
  commands: Command[];
}

export class Sequence {
  public readonly seqId: string;
  public readonly metadata: Record<string, any>;
  public readonly commands: Command[];

  private constructor(opts: SequenceOptions) {
    this.seqId = opts.seqId;
    this.metadata = opts.metadata;
    this.commands = opts.commands;
  }

  public static new(opts: SequenceOptions): Sequence {
    return new Sequence(opts);
  }

  public toSeqJson(): SequenceSeqJson {
    return {
      id: this.seqId,
      metadata: this.metadata,
      steps: this.commands.map(c => c.toSeqJson()),
    };
  }

  public toEDSLString(): string {

    const commandsString = this.commands.length === 0
        ? ''
        : '\n' + indent(this.commands.map(c => c.toEDSLString() + ',').join('\n'), 3);

  return (
`export default () =>
  Sequence.new({
    seqId: '${this.seqId}',
    metadata: ${JSON.stringify(this.metadata)},
    commands: [${commandsString}
    ],
  });`
  );
  }

  public static fromSeqJson(json: SequenceSeqJson): Sequence {
    return Sequence.new({
      seqId: json.id,
      metadata: json.metadata,
      commands: json.steps.map(c => Command.fromSeqJson(c)),
    });
  }
}

/** Time utilities */

export type DOY_STRING = string & { __brand: 'DOY_STRING' };
export type HMS_STRING = string & { __brand: 'HMS_STRING' };

const DOY_REGEX = /(\d{4})-(\d{3})T(\d{2}):(\d{2}):(\d{2})(?:\.(\d{3}))?/;
const HMS_REGEX = /(\d{2}):(\d{2}):(\d{2})(?:\.(\d{3}))?/;

/** YYYY-DOYTHH:MM:SS.sss */
export function instantToDoy(time: Temporal.Instant): DOY_STRING {
  const utcZonedDate = time.toZonedDateTimeISO('UTC');
  const YYYY = formatNumber(utcZonedDate.year, 4);
  const DOY = formatNumber(utcZonedDate.dayOfYear, 3);
  const HH = formatNumber(utcZonedDate.hour, 2);
  const MM = formatNumber(utcZonedDate.minute, 2);
  const SS = formatNumber(utcZonedDate.second, 2);
  const sss = formatNumber(utcZonedDate.millisecond, 3);
  return `${YYYY}-${DOY}T${HH}:${MM}:${SS}.${sss}` as DOY_STRING;
}

export function doyToInstant(doy: DOY_STRING): Temporal.Instant {
  const match = doy.match(DOY_REGEX);
  if (match === null) {
    throw new Error(`Invalid DOY string: ${doy}`);
  }
  const [, year, doyStr, hour, minute, second, millisecond] = match as [
    unknown,
    string,
    string,
    string,
    string,
    string,
        string | undefined,
  ];

  //use to convert doy to month and day
  const doyDate = new Date(parseInt(year, 10), 0, parseInt(doyStr, 10));
  // convert to UTC Date
  const utcDoyDate = new Date(
      Date.UTC(
          doyDate.getUTCFullYear(),
          doyDate.getUTCMonth(),
          doyDate.getUTCDate(),
          doyDate.getUTCHours(),
          doyDate.getUTCMinutes(),
          doyDate.getUTCSeconds(),
          doyDate.getUTCMilliseconds(),
      ),
  );

  return Temporal.ZonedDateTime.from({
    year: parseInt(year, 10),
    month: utcDoyDate.getUTCMonth() + 1,
    day: utcDoyDate.getUTCDate(),
    hour: parseInt(hour, 10),
    minute: parseInt(minute, 10),
    second: parseInt(second, 10),
    millisecond: parseInt(millisecond ?? '0', 10),
    timeZone: 'UTC',
  }).toInstant();
}

/** HH:MM:SS.sss */
export function durationToHms(time: Temporal.Duration): HMS_STRING {
  const HH = formatNumber(time.hours, 2);
  const MM = formatNumber(time.minutes, 2);
  const SS = formatNumber(time.seconds, 2);
  const sss = formatNumber(time.milliseconds, 3);

  return `${HH}:${MM}:${SS}.${sss}` as HMS_STRING;
}

export function hmsToDuration(hms: HMS_STRING): Temporal.Duration {
  const match = hms.match(HMS_REGEX);
  if (match === null) {
    throw new Error(`Invalid HMS string: ${hms}`);
  }
  const [, hours, minutes, seconds, milliseconds] = match as [unknown, string, string, string, string | undefined];
  return Temporal.Duration.from({
    hours: parseInt(hours, 10),
    minutes: parseInt(minutes, 10),
    seconds: parseInt(seconds, 10),
    milliseconds: parseInt(milliseconds ?? '0', 10),
  });
}

function formatNumber(number: number, size: number): string {
  return number.toString().padStart(size, '0');
}

// @ts-ignore : Used in generated code
function A(...args: [TemplateStringsArray, ...string[]] | [Temporal.Instant] | [string]): typeof Commands {
  let time: Temporal.Instant;
  if (Array.isArray(args[0])) {
    time = doyToInstant(String.raw(...(args as [TemplateStringsArray, ...string[]])) as DOY_STRING);
  } else if (typeof args[0] === 'string') {
    time = doyToInstant(args[0] as DOY_STRING);
  } else {
    time = args[0] as Temporal.Instant;
  }

  return commandsWithTimeValue(time, TimingTypes.ABSOLUTE);
}

// @ts-ignore : Used in generated code
function R(...args: [TemplateStringsArray, ...string[]] | [Temporal.Duration] | [string]): typeof Commands {
  let duration: Temporal.Duration;
  if (Array.isArray(args[0])) {
    duration = hmsToDuration(String.raw(...(args as [TemplateStringsArray, ...string[]])) as HMS_STRING);
  } else if (typeof args[0] === 'string') {
    duration = hmsToDuration(args[0] as HMS_STRING);
  } else {
    duration = args[0] as Temporal.Duration;
  }

  return commandsWithTimeValue(duration, TimingTypes.COMMAND_RELATIVE);
}

// @ts-ignore : Used in generated code
function E(...args: [TemplateStringsArray, ...string[]] | [Temporal.Duration] | [string]): typeof Commands {
  let duration: Temporal.Duration;
  if (Array.isArray(args[0])) {
    duration = hmsToDuration(String.raw(...(args as [TemplateStringsArray, ...string[]])) as HMS_STRING);
  } else if (typeof args[0] === 'string') {
    duration = hmsToDuration(args[0] as HMS_STRING);
  } else {
    duration = args[0] as Temporal.Duration;
  }
  return commandsWithTimeValue(duration, TimingTypes.EPOCH_RELATIVE);
}

function commandsWithTimeValue<T extends TimingTypes>(
  timeValue: Temporal.Instant | Temporal.Duration,
  timeType: T,
  // @ts-ignore : 'Commands' found in generated code
): typeof Commands {
  // @ts-ignore : 'Commands' found in generated code
  return Object.keys(Commands).reduce((accum, key) => {
    // @ts-ignore : 'Commands' found in generated code
    const command = Commands[key as keyof Commands];
    if (typeof command === 'function') {
      if (timeType === TimingTypes.ABSOLUTE) {
        accum[key] = (...args: Parameters<typeof command>): typeof command => {
          return command(...args).absoluteTiming(timeValue);
        };
      } else if (timeType === TimingTypes.COMMAND_RELATIVE) {
        accum[key] = (...args: Parameters<typeof command>): typeof command => {
          return command(...args).relativeTiming(timeValue);
        };
      } else {
        accum[key] = (...args: Parameters<typeof command>): typeof command => {
          return command(...args).epochTiming(timeValue);
        };
      }
    } else {
      if (timeType === TimingTypes.ABSOLUTE) {
        accum[key] = command.absoluteTiming(timeValue);
      } else if (timeType === TimingTypes.COMMAND_RELATIVE) {
        accum[key] = command.relativeTiming(timeValue);
      } else {
        accum[key] = command.epochTiming(timeValue);
      }
    }
    return accum;
    // @ts-ignore : 'Commands' found in generated code
  }, {} as typeof Commands);
}

// @ts-ignore
function orderCommandArguments(args: { [argName: string]: any }, order: string[]): any {
  return order.map(key => args[key]);
}

// @ts-ignore: Used in generated code
function findAndOrderCommandArguments(
  commandName: string,
  args: { [argName: string]: any },
  argumentOrders: string[][],
): any {
  for (const argumentOrder of argumentOrders) {
    if (argumentOrder.length === Object.keys(args).length) {
      let difference = argumentOrder
        .filter((value: string) => !Object.keys(args).includes(value))
        .concat(Object.keys(args).filter((value: string) => !argumentOrder.includes(value))).length;

      // found correct argument order to apply
      if (difference === 0) {
        return orderCommandArguments(args, argumentOrder);
      }
    }
  }
  throw new Error(`Could not find correct argument order for command: ${commandName}`);
}

function indent(text: string, numTimes: number = 1, char: string = '  '): string {
  return text
    .split('\n')
    .map(line => char.repeat(numTimes) + line)
    .join('\n');
}

/** END Preface */
