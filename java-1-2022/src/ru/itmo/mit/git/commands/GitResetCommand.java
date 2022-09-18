package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.body.context.GitMutableOperation;
import ru.itmo.mit.git.entities.GitBranch;
import ru.itmo.mit.git.entities.GitEntityType;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.List;

public class GitResetCommand extends GitAbstractCommand {

    public GitResetCommand(GitContext gitContext) {
        super(gitContext);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        loadState();
        if (args.get(0).contains(GitConstants.HEAD)) {
            String[] splitArgs = args.get(0).split("~");
            if (splitArgs.length < 2) {
                throw new GitException("Wrong syntax for reset from HEAD ");
            }
            Reset.execute(gitContext, gitContext.getGitRepoManager().
                    getHashRevision(gitContext, Integer.parseInt(splitArgs[1])));
        } else {
            Reset.execute(gitContext, args.get(0));
        }
        updateState(gitContext.getGitRepoManager().getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        outputStream.println(CommandMessages.RESET_MESSAGE.getMessage() + args.get(0));
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("No revision provided ");
        }
        if (args.size() > 1) {
            throw new GitException("Wrong number of arguments for reset command ");
        }
    }

    private final GitMutableOperation Reset = new GitMutableOperation() {
        private void moveHeadTo(@NotNull String hash) throws GitException {
            if (gitContext.getGitRepoManager().getHead().getTypeEntity().equals(GitEntityType.BRANCH)) {
                GitBranch branch = (GitBranch) gitContext.getGitRepoManager().getHead();
                branch.setBranchHash(hash);
                gitContext.getGitRepoManager().setHead(branch);
            }
            if (gitContext.getGitRepoManager().getHead().getTypeEntity().equals(GitEntityType.COMMIT)) {
                gitContext.getGitRepoManager().setHead
                        (gitContext.getGitFileManager().getGitCommitFileManager().readByHash(hash));
            }
        }

        private void moveHeadAndBranchTo(@NotNull String hash) throws GitException {
            moveHeadTo(hash);
            if (gitContext.getGitRepoManager().getHead().getTypeEntity().equals(GitEntityType.BRANCH)) {
                GitBranch branch = (GitBranch) gitContext.getGitRepoManager().getHead();
                gitContext.getGitFileManager().getGitBranchFileManager().
                        updateBranch(branch.getBranchName(), branch.getHash());
            }
        }

        @Override
        public void execute(@NotNull GitContext gitContext, String arg) throws GitException {
            moveHeadAndBranchTo(arg);
            gitContext.getGitIndexStateManager().loadIndexStateFromRepo(gitContext);
            gitContext.getGitWorkDirStateHelper().loadWorkDirStateFromRepo(gitContext);
        }
    };
}
