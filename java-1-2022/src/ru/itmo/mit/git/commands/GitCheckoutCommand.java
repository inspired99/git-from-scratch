package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.body.context.GitIndexManager;
import ru.itmo.mit.git.body.context.GitMutableOperation;
import ru.itmo.mit.git.entities.GitBlob;
import ru.itmo.mit.git.entities.GitEntity;
import ru.itmo.mit.git.utils.CommandMessages;
import ru.itmo.mit.git.utils.FileManager;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class GitCheckoutCommand extends GitAbstractCommand {
    public GitCheckoutCommand(GitContext gitContext) {
        super(gitContext);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        loadState();
        if (args.get(0).equals(GitConstants.CHECKOUT_FILES_DASH)) {
            for (int i = 1; i < args.size(); ++i) {
                CheckoutFile.execute(gitContext, args.get(i));
            }
        } else {
            if (args.get(0).contains(GitConstants.HEAD)) {
                String[] splitArgs = args.get(0).split("~");
                if (splitArgs.length < 2) {
                    throw new GitException("Wrong syntax for checkout from HEAD ");
                }
                CheckoutToRevision.execute(gitContext, gitContext.getGitRepoManager().
                        getHashRevision(gitContext, Integer.parseInt(splitArgs[1])));
            } else {
                CheckoutToRevision.execute(gitContext, args.get(0));
            }
        }
        updateState(gitContext.getGitRepoManager().getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        if (args.get(0).equals(GitConstants.CHECKOUT_FILES_DASH)) {
            outputStream.println(CommandMessages.CHECKOUT_FILES_MESSAGE.getMessage() +
                    String.join("\n", args.subList(1, args.size())));
        } else {
            outputStream.println(CommandMessages.CHECKOUT_MESSAGE.getMessage() + args.get(0));
        }
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("Not enough arguments for checkout command ");
        }
        if (args.get(0).equals(GitConstants.CHECKOUT_FILES_DASH)) {
            if (args.size() < 2) {
                throw new GitException("No files provided to checkout ");
            }
        } else {
            if (args.size() > 1) {
                throw new GitException("Wrong number of arguments for checkout command ");
            }
        }
    }

    private final GitMutableOperation CheckoutFile = (gitContext, arg) -> {
        String pathToFile = gitContext.getGitFileManager().pathInGitDir(Path.of(arg)).toString();
        GitIndexManager index = gitContext.getGitIndex();
        HashMap<String, String> commitFiles = gitContext.getGitRepoManager().makeRootTreeSnapshot(gitContext).getBlobsHashes();
        if (!commitFiles.containsKey(pathToFile)) {
            throw new GitException("No file version to checkout ");
        }
        if (index.getGitIndexTracked().containsKey(pathToFile)) {
            index.getGitIndexTracked().put(pathToFile, commitFiles.get(pathToFile));
        }
        FileManager.deleteFileByPath(Path.of(pathToFile));
        GitBlob blob = gitContext.getGitFileManager().getGitBlobFileManager().readByHash(commitFiles.get(pathToFile));
        FileManager.writeFileByPath(Path.of(pathToFile), blob.getContentBlob());
    };

    private final GitMutableOperation CheckoutToRevision = (gitContext, arg) -> {
        GitEntity nextHead;
        if (gitContext.getGitFileManager().getGitBranchFileManager().checkBranchExists(arg)) {
            nextHead = gitContext.getGitFileManager().getGitBranchFileManager().getBranchByName(arg);
        } else {
            nextHead = gitContext.getGitFileManager().getGitCommitFileManager()
                    .readByHash((arg));
        }
        gitContext.getGitRepoManager().setHead(nextHead);
        gitContext.getGitIndexStateManager().loadIndexStateFromRepo(gitContext);
        gitContext.getGitWorkDirStateHelper().loadWorkDirStateFromRepo(gitContext);
    };
}
