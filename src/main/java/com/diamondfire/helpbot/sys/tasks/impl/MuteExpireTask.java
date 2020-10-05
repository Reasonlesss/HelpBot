package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.impl.other.MuteCommand;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.tasks.OneTimeTask;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.*;
import java.time.*;
import java.util.Date;

public class MuteExpireTask implements OneTimeTask {

    private final long ms;
    private final long member;


    public MuteExpireTask(long member, Date date) {
        this.ms = Duration.between(Instant.now(), date.toInstant()).toMillis();
        this.member = member;
    }

    @Override
    public long getExecution() {
        return ms;
    }

    @Override
    public void run() {
        Guild guild = HelpBotInstance.getJda().getGuildById(HelpBotInstance.DF_GUILD);
        guild.removeRoleFromMember(member, guild.getRoleById(MuteCommand.ROLE_ID)).queue();

        new DatabaseQuery()
                .query(new BasicQuery("UPDATE owen.muted_members SET handled = true WHERE member = ?", (statement) -> statement.setLong(1, member)))
                .compile();
    }

    public static void prepare() {
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM owen.muted_members WHERE (muted_till > CURRENT_TIMESTAMP() || !handled)"))
                .compile()
                .run((result) -> {
                    // Select unique names.
                    for (ResultSet set : result) {
                        Timestamp date = set.getTimestamp("muted_till");
                        long member = set.getLong("member");

                        HelpBotInstance.getScheduler().schedule(new MuteExpireTask(member, date));
                    }
                });
    }
}