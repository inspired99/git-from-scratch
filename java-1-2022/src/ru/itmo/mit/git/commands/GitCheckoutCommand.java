package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.List;

public class GitCheckoutCommand extends GitAbstractCommand {
    public GitCheckoutCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        gitManager.loadGitState();
        if (args.get(0).equals(GitConstants.CHECKOUT_FILES_DASH)) {
            for (int i = 1; i < args.size(); ++i) {
                gitManager.checkoutFile(args.get(i));
            }
        } else {
            if (args.get(0).contains(GitConstants.HEAD)) {
                String[] splitArgs = args.get(0).split("~");
                if (splitArgs.length < 2) {
                    throw new GitException("Wrong syntax for checkout from HEAD ");
                }
                gitManager.checkoutToRevision(gitManager.getRevisionFromHead(Integer.parseInt(splitArgs[1])));
            } else {
                gitManager.checkoutToRevision(args.get(0));
            }
        }
        gitManager.saveGitState(gitManager.getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        if (args.get(0).equals(GitConstants.CHECKOUT_FILES_DASH)) {
            gitManager.getPrintStream().println(CommandMessages.CHECKOUT_FILES_MESSAGE.getMessage() +
                    String.join("\n", args.subList(1, args.size())));
        } else {
            gitManager.getPrintStream().println(CommandMessages.CHECKOUT_MESSAGE.getMessage() + args.get(0));
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
}
