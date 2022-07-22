package com.yj2025.basic.service;

import com.yj2025.basic.command.Command;
import io.vavr.*;
import io.vavr.control.Option;

public abstract class BasicService {

    /**
     * 执行一个cmd命令
     * @param command
     */
    protected final void execute(Command command) {
        command.execute();
    }

    protected final void execute(Command... commands) {
        for (Command command : commands) {
            command.execute();
        }
    }

    protected final void executeWhen(boolean predicate, Command command) {
        Option.when(predicate, () -> command.execute());
    }

    protected final void executeWhen(Tuple2<Boolean, Command>... tuple2s) {
        for (Tuple2<Boolean, Command> tuple2 : tuple2s) {
            if (tuple2._1() != null && tuple2._1()) {
                tuple2._2().execute();
            }
        }
    }

    protected final <T1> T1 executeReturn(Command<T1> tCommand) {
        return tCommand.execute();
    }

    protected final <T1, T2> Tuple2<T1, T2> executeReturn(Command<T1> t1Command, Command<T2> t2Command) {
        return Tuple.of(t1Command.execute(), t2Command.execute());
    }

    protected final <T1, T2, T3> Tuple3<T1, T2, T3> executeReturn(Command<T1> t1Command, Command<T2> t2Command, Command<T3> t3Command) {
        return Tuple.of(t1Command.execute(), t2Command.execute(), t3Command.execute());
    }

    protected final <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> executeReturn(Command<T1> t1Command, Command<T2> t2Command, Command<T3> t3Command, Command<T4> t4Command) {
        return Tuple.of(t1Command.execute(), t2Command.execute(), t3Command.execute(), t4Command.execute());
    }

    protected final <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> executeReturn(Command<T1> t1Command, Command<T2> t2Command, Command<T3> t3Command, Command<T4> t4Command, Command<T5> t5Command) {
        return Tuple.of(t1Command.execute(), t2Command.execute(), t3Command.execute(), t4Command.execute(), t5Command.execute());
    }

    protected final <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> executeReturn(Command<T1> t1Command, Command<T2> t2Command, Command<T3> t3Command, Command<T4> t4Command, Command<T5> t5Command, Command<T6> t6Command) {
        return Tuple.of(t1Command.execute(), t2Command.execute(), t3Command.execute(), t4Command.execute(), t5Command.execute(), t6Command.execute());
    }

    protected final <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> executeReturn(Command<T1> t1Command, Command<T2> t2Command, Command<T3> t3Command, Command<T4> t4Command, Command<T5> t5Command, Command<T6> t6Command, Command<T7> t7Command) {
        return Tuple.of(t1Command.execute(), t2Command.execute(), t3Command.execute(), t4Command.execute(), t5Command.execute(), t6Command.execute(), t7Command.execute());
    }

    protected final <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> executeReturn(Command<T1> t1Command, Command<T2> t2Command, Command<T3> t3Command, Command<T4> t4Command, Command<T5> t5Command, Command<T6> t6Command, Command<T7> t7Command, Command<T8> t8Command) {
        return Tuple.of(t1Command.execute(), t2Command.execute(), t3Command.execute(), t4Command.execute(), t5Command.execute(), t6Command.execute(), t7Command.execute(), t8Command.execute());
    }

    protected final <T1> T1 executeReturnWhen(boolean predicate, Command<T1> t1Command) {
        return Option.when(predicate, t1Command.execute()).get();
    }

    protected final <T1> T1 executeReturnWhen(Tuple2<Boolean, Command<T1>> t1Command) {
        return Option.when(t1Command._1(), t1Command._2().execute()).get();
    }

    protected final <T1, T2> Tuple2<T1, T2> executeReturnWhen(Tuple2<Boolean, Command<T1>> t1Command, Tuple2<Boolean, Command<T2>> t2Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).get(),
                Option.when(t2Command._1(), t2Command._2().execute()).get()
        );
    }

    protected final <T1, T2, T3> Tuple3<T1, T2, T3> executeReturnWhen(Tuple2<Boolean, Command<T1>> t1Command, Tuple2<Boolean, Command<T2>> t2Command, Tuple2<Boolean, Command<T3>> t3Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).get(),
                Option.when(t2Command._1(), t2Command._2().execute()).get(),
                Option.when(t3Command._1(), t3Command._2().execute()).get()
        );
    }

    protected final <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> executeReturnWhen(Tuple2<Boolean, Command<T1>> t1Command, Tuple2<Boolean, Command<T2>> t2Command, Tuple2<Boolean, Command<T3>> t3Command, Tuple2<Boolean, Command<T4>> t4Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).get(),
                Option.when(t2Command._1(), t2Command._2().execute()).get(),
                Option.when(t3Command._1(), t3Command._2().execute()).get(),
                Option.when(t4Command._1(), t4Command._2().execute()).get()
        );
    }

    protected final <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> executeReturnWhen(Tuple2<Boolean, Command<T1>> t1Command, Tuple2<Boolean, Command<T2>> t2Command, Tuple2<Boolean, Command<T3>> t3Command, Tuple2<Boolean, Command<T4>> t4Command, Tuple2<Boolean, Command<T5>> t5Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).get(),
                Option.when(t2Command._1(), t2Command._2().execute()).get(),
                Option.when(t3Command._1(), t3Command._2().execute()).get(),
                Option.when(t4Command._1(), t4Command._2().execute()).get(),
                Option.when(t5Command._1(), t5Command._2().execute()).get()
        );
    }

    protected final <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> executeReturnWhen(Tuple2<Boolean, Command<T1>> t1Command, Tuple2<Boolean, Command<T2>> t2Command, Tuple2<Boolean, Command<T3>> t3Command, Tuple2<Boolean, Command<T4>> t4Command, Tuple2<Boolean, Command<T5>> t5Command, Tuple2<Boolean, Command<T6>> t6Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).get(),
                Option.when(t2Command._1(), t2Command._2().execute()).get(),
                Option.when(t3Command._1(), t3Command._2().execute()).get(),
                Option.when(t4Command._1(), t4Command._2().execute()).get(),
                Option.when(t5Command._1(), t5Command._2().execute()).get(),
                Option.when(t6Command._1(), t6Command._2().execute()).get()
        );
    }

    protected final <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> executeReturnWhen(Tuple2<Boolean, Command<T1>> t1Command, Tuple2<Boolean, Command<T2>> t2Command, Tuple2<Boolean, Command<T3>> t3Command, Tuple2<Boolean, Command<T4>> t4Command, Tuple2<Boolean, Command<T5>> t5Command, Tuple2<Boolean, Command<T6>> t6Command, Tuple2<Boolean, Command<T7>> t7Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).get(),
                Option.when(t2Command._1(), t2Command._2().execute()).get(),
                Option.when(t3Command._1(), t3Command._2().execute()).get(),
                Option.when(t4Command._1(), t4Command._2().execute()).get(),
                Option.when(t5Command._1(), t5Command._2().execute()).get(),
                Option.when(t6Command._1(), t6Command._2().execute()).get(),
                Option.when(t7Command._1(), t7Command._2().execute()).get()
        );
    }

    protected final <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> executeReturnWhen(Tuple2<Boolean, Command<T1>> t1Command, Tuple2<Boolean, Command<T2>> t2Command, Tuple2<Boolean, Command<T3>> t3Command, Tuple2<Boolean, Command<T4>> t4Command, Tuple2<Boolean, Command<T5>> t5Command, Tuple2<Boolean, Command<T6>> t6Command, Tuple2<Boolean, Command<T7>> t7Command, Tuple2<Boolean, Command<T8>> t8Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).get(),
                Option.when(t2Command._1(), t2Command._2().execute()).get(),
                Option.when(t3Command._1(), t3Command._2().execute()).get(),
                Option.when(t4Command._1(), t4Command._2().execute()).get(),
                Option.when(t5Command._1(), t5Command._2().execute()).get(),
                Option.when(t6Command._1(), t6Command._2().execute()).get(),
                Option.when(t7Command._1(), t7Command._2().execute()).get(),
                Option.when(t8Command._1(), t8Command._2().execute()).get()
        );
    }

    protected final <T> T executeReturnWhen(boolean predicate, Command<T> command, T defaultValue) {
        return Option.when(predicate, command.execute()).getOrElse(defaultValue);
    }

    protected final <T1> T1 executeReturnWhen(Tuple3<Boolean, Command<T1>, T1> t1Command) {
        return Option.when(t1Command._1(), t1Command._2().execute()).getOrElse(t1Command._3());
    }


    protected final <T1, T2> Tuple2<T1, T2> executeReturnWhen(Tuple3<Boolean, Command<T1>, T1> t1Command, Tuple3<Boolean, Command<T2>, T2> t2Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).getOrElse(t1Command._3()),
                Option.when(t2Command._1(), t2Command._2().execute()).getOrElse(t2Command._3())
        );
    }

    protected final <T1, T2, T3> Tuple3<T1, T2, T3> executeReturnWhen(Tuple3<Boolean, Command<T1>, T1> t1Command, Tuple3<Boolean, Command<T2>, T2> t2Command, Tuple3<Boolean, Command<T3>, T3> t3Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).getOrElse(t1Command._3()),
                Option.when(t2Command._1(), t2Command._2().execute()).getOrElse(t2Command._3()),
                Option.when(t3Command._1(), t3Command._2().execute()).getOrElse(t3Command._3())
        );
    }

    protected final <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> executeReturnWhen(Tuple3<Boolean, Command<T1>, T1> t1Command, Tuple3<Boolean, Command<T2>, T2> t2Command, Tuple3<Boolean, Command<T3>, T3> t3Command, Tuple3<Boolean, Command<T4>, T4> t4Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).getOrElse(t1Command._3()),
                Option.when(t2Command._1(), t2Command._2().execute()).getOrElse(t2Command._3()),
                Option.when(t3Command._1(), t3Command._2().execute()).getOrElse(t3Command._3()),
                Option.when(t4Command._1(), t4Command._2().execute()).getOrElse(t4Command._3())
        );
    }

    protected final <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> executeReturnWhen(Tuple3<Boolean, Command<T1>, T1> t1Command, Tuple3<Boolean, Command<T2>, T2> t2Command, Tuple3<Boolean, Command<T3>, T3> t3Command, Tuple3<Boolean, Command<T4>, T4> t4Command, Tuple3<Boolean, Command<T5>, T5> t5Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).getOrElse(t1Command._3()),
                Option.when(t2Command._1(), t2Command._2().execute()).getOrElse(t2Command._3()),
                Option.when(t3Command._1(), t3Command._2().execute()).getOrElse(t3Command._3()),
                Option.when(t4Command._1(), t4Command._2().execute()).getOrElse(t4Command._3()),
                Option.when(t5Command._1(), t5Command._2().execute()).getOrElse(t5Command._3())
        );
    }

    protected final <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> executeReturnWhen(Tuple3<Boolean, Command<T1>, T1> t1Command, Tuple3<Boolean, Command<T2>, T2> t2Command, Tuple3<Boolean, Command<T3>, T3> t3Command, Tuple3<Boolean, Command<T4>, T4> t4Command, Tuple3<Boolean, Command<T5>, T5> t5Command, Tuple3<Boolean, Command<T6>, T6> t6Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).getOrElse(t1Command._3()),
                Option.when(t2Command._1(), t2Command._2().execute()).getOrElse(t2Command._3()),
                Option.when(t3Command._1(), t3Command._2().execute()).getOrElse(t3Command._3()),
                Option.when(t4Command._1(), t4Command._2().execute()).getOrElse(t4Command._3()),
                Option.when(t5Command._1(), t5Command._2().execute()).getOrElse(t5Command._3()),
                Option.when(t6Command._1(), t6Command._2().execute()).getOrElse(t6Command._3())
        );
    }

    protected final <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> executeReturnWhen(Tuple3<Boolean, Command<T1>, T1> t1Command, Tuple3<Boolean, Command<T2>, T2> t2Command, Tuple3<Boolean, Command<T3>, T3> t3Command, Tuple3<Boolean, Command<T4>, T4> t4Command, Tuple3<Boolean, Command<T5>, T5> t5Command, Tuple3<Boolean, Command<T6>, T6> t6Command, Tuple3<Boolean, Command<T7>, T7> t7Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).getOrElse(t1Command._3()),
                Option.when(t2Command._1(), t2Command._2().execute()).getOrElse(t2Command._3()),
                Option.when(t3Command._1(), t3Command._2().execute()).getOrElse(t3Command._3()),
                Option.when(t4Command._1(), t4Command._2().execute()).getOrElse(t4Command._3()),
                Option.when(t5Command._1(), t5Command._2().execute()).getOrElse(t5Command._3()),
                Option.when(t6Command._1(), t6Command._2().execute()).getOrElse(t6Command._3()),
                Option.when(t7Command._1(), t7Command._2().execute()).getOrElse(t7Command._3())
        );
    }

    protected final <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> executeReturnWhen(Tuple3<Boolean, Command<T1>, T1> t1Command, Tuple3<Boolean, Command<T2>, T2> t2Command, Tuple3<Boolean, Command<T3>, T3> t3Command, Tuple3<Boolean, Command<T4>, T4> t4Command, Tuple3<Boolean, Command<T5>, T5> t5Command, Tuple3<Boolean, Command<T6>, T6> t6Command, Tuple3<Boolean, Command<T7>, T7> t7Command, Tuple3<Boolean, Command<T8>, T8> t8Command) {
        return Tuple.of(
                Option.when(t1Command._1(), t1Command._2().execute()).getOrElse(t1Command._3()),
                Option.when(t2Command._1(), t2Command._2().execute()).getOrElse(t2Command._3()),
                Option.when(t3Command._1(), t3Command._2().execute()).getOrElse(t3Command._3()),
                Option.when(t4Command._1(), t4Command._2().execute()).getOrElse(t4Command._3()),
                Option.when(t5Command._1(), t5Command._2().execute()).getOrElse(t5Command._3()),
                Option.when(t6Command._1(), t6Command._2().execute()).getOrElse(t6Command._3()),
                Option.when(t7Command._1(), t7Command._2().execute()).getOrElse(t7Command._3()),
                Option.when(t8Command._1(), t8Command._2().execute()).getOrElse(t8Command._3())
        );
    }


}
