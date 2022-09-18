package ru.itmo.mit.git.objects;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Calendar;

public class GitCommit extends GitObject{
    private final String messageCommit;
    private final String dateCommit;
    private final String parentCommitHash;
    private final String rootTreeHash;
    private final String authorCommit;

    public GitCommit(String authorCommit, String messageCommit, String parentCommitHash, String rootTreeHash) {
        super(GitObjectType.COMMIT);
        this.authorCommit = authorCommit;
        this.messageCommit = messageCommit;
        this.dateCommit = currentDate();
        this.parentCommitHash = parentCommitHash;
        this.rootTreeHash = rootTreeHash;
        countHash(typeObject.getType() + messageCommit + authorCommit + dateCommit + rootTreeHash);
    }

    private @NotNull String currentDate() {
        return DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    }

    public String getMessageCommit() {
        return messageCommit;
    }

    public String getDateCommit() {
        return dateCommit;
    }

    public String getParentCommitHash() {
        return parentCommitHash;
    }

    public String getRootTreeHash() {
        return rootTreeHash;
    }

    public String getAuthorCommit() {
        return authorCommit;
    }
}
