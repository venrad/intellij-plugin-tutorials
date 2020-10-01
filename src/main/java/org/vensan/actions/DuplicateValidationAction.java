package org.vensan.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class DuplicateValidationAction extends AnAction {
  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    //Based on local File system
    localFileSystemApproach(e);

    // FileBasedIndex approach??
  }

  private void localFileSystemApproach(AnActionEvent e) {
    String basePath = "/tmp/workarea";

    VirtualFile rootDir = LocalFileSystem.getInstance().findFileByIoFile(new File(basePath));
    MultiMap<String, VirtualFile> checkingOnDuplicates = new MultiMap<>();
    VfsUtilCore.iterateChildrenRecursively(rootDir, null, new ContentIterator() {
      @Override
      public boolean processFile(@NotNull VirtualFile fileOrDir) {
        if (!(fileOrDir.isDirectory() && fileOrDir.getName().equals(".git"))) {
          if (!fileOrDir.isDirectory() && fileOrDir.getName().endsWith(".csv")) {
            try {
              System.out.println(fileOrDir.getName());
              checkingOnDuplicates.putValue(new String(fileOrDir.contentsToByteArray()),
                  fileOrDir);
            } catch (IOException ioException) {
              ioException.printStackTrace();
            }
          }
        }
        return true;
      }
    });
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, Collection<VirtualFile>> valu : checkingOnDuplicates.entrySet()) {
      String content = valu.getKey();
      Collection<VirtualFile> fileNames = valu.getValue();
      if (fileNames.size()> 1) {
        StringBuilder b = new StringBuilder();
        for (VirtualFile file: fileNames) {
          if (b.length() !=0) b.append(", ");
          b.append(file.getPath());
        }
        builder.append(String.format("%s are all duplicates\n",b.toString()));
      }
    }
    Messages.showInfoMessage(builder.toString().isEmpty()? "No Duplicates found":builder.toString(),
        "Duplicate Files");
  }

}
