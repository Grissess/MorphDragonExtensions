package mods.grissess.mde.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class TransformEntityReach implements IClassTransformer {
	public static final String
		ENTITY_RENDERER = "net.minecraft.client.renderer.EntityRenderer";
	public static final String METHOD_NAME_UNOB = "getMouseOver";
	public static final String METHOD_NAME_OB = "func_78473_a";

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(!ENTITY_RENDERER.equals(transformedName)) {
			return basicClass;
		}
		ClassReader reader = new ClassReader(basicClass);
		ClassNode cls = new ClassNode();
		reader.accept(cls, 0);
		
		boolean found = false;
		for(MethodNode meth: cls.methods) {
			String methName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, meth.name, meth.desc);
			if(methName.equals(METHOD_NAME_UNOB) || methName.equals(METHOD_NAME_OB)) {
				System.out.println(String.format("TER.t: found %s::%s as %s", ENTITY_RENDERER, METHOD_NAME_UNOB, methName));
				found = true;
				boolean seenFirstIfEq = false, success = false;
				for(int idx = 0; idx < meth.instructions.size(); idx++) {
					AbstractInsnNode insn = meth.instructions.get(idx);
					/*
					 * Patch 1: if(this.mc.playerController.extendedReach())
					 * compiles to an IFEQ (effective jump-if-false) at 94,
					 * the first in the routine; change to IFGE will cause
					 * this to always jump to the else (false) branch.
					 */
					if(!seenFirstIfEq && insn.getOpcode() == Opcodes.IFEQ) {
						seenFirstIfEq = true;
						((JumpInsnNode) insn).setOpcode(Opcodes.IFGE);
						System.out.println("TER.t: patch 1 applied");
					}
					/*
					 * Patch 2: flag = true; is iconst_1; istore 6;
					 * immediately following the above. To keep flag false
					 * (and thus not null out the pick entity), load 0
					 * (false) instead.
					 */
					if(seenFirstIfEq && insn.getOpcode() == Opcodes.ICONST_1) {
						meth.instructions.set(insn, new InsnNode(Opcodes.ICONST_0));
						System.out.println("TER.t: patch 2 applied");
						success = true;
						break;
					}
				}
				if(!success) {
					System.out.println("TER.t: PATCHING FAILED! Returning to normal operation...");
					return basicClass;
				}
			}
		}
		if(!found) {
			System.out.println("TER.t: METHOD NOT FOUND! Returning to normal operation...");
			return basicClass;
		}

		System.out.println("TER.t: patch succeeded");
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		cls.accept(writer);
		return writer.toByteArray();
	}
}
