package io.github.zekerzhayard.cme_jsonhelper.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("spazley.scalingguis.config.JsonHelper".equals(transformedName)) {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);
            for (MethodNode mn : cn.methods) {
                if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "getKeyList") && RemapUtils.checkMethodDesc(mn.desc, "(Lcom/google/gson/JsonObject;)Ljava/util/List;")) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
                            MethodInsnNode min = (MethodInsnNode) ain;
                            if (RemapUtils.checkClassName(min.owner, "java/util/ArrayList") && RemapUtils.checkMethodName(min.owner, min.name, min.desc, "<init>") && RemapUtils.checkMethodDesc(min.desc, "(Ljava/util/Collection;)V")) {
                                mn.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/cme_jsonhelper/CopyOnWriteArrayListWithMutableIterator", "create", "(Ljava/util/Collection;)Ljava/util/List;", false));
                            }
                        }
                    }
                }
            }
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            basicClass = cw.toByteArray();
        }
        return basicClass;
    }
}
