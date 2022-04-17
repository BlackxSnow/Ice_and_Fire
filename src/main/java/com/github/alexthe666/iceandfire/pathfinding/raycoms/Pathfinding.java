package com.github.alexthe666.iceandfire.pathfinding.raycoms;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.pathjobs.AbstractPathJob;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.util.thread.BlockableEventLoop;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import org.lwjgl.opengl.GL11;

import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.concurrent.*;

import static com.github.alexthe666.iceandfire.pathfinding.raycoms.PathfindingConstants.debugNodeMonitor;

/**
 * Static class the handles all the Pathfinding.
 */
public final class Pathfinding {
    private static final Set<Class<?>> loadedJobs = new CopyOnWriteArraySet<>();
    private static final BlockingQueue<Runnable> jobQueue = new LinkedBlockingDeque<>();
    private static ThreadPoolExecutor executor;

    private Pathfinding() {
        //Hides default constructor.
    }

    public static boolean isDebug() {
        return false;
    }

    /**
     * Creates a new thread pool for pathfinding jobs
     *
     * @return the threadpool executor.
     */
    public static ThreadPoolExecutor getExecutor() {
        if (executor == null) {
            executor = new ThreadPoolExecutor(1, IafConfig.dragonPathfindingThreads, 10, TimeUnit.SECONDS, jobQueue, new IafThreadFactory());
        }
        return executor;
    }

    /**
     * Stops all running threads in this thread pool
     */
    public static void shutdown() {
        getExecutor().shutdownNow();
        jobQueue.clear();
        executor = null;
    }

    /**
     * Add a job to the queue for processing.
     *
     * @param job PathJob
     * @return a Future containing the Path
     */
    public static Future<Path> enqueue(final AbstractPathJob job) {
        if (!loadedJobs.contains(job.getClass())) {
            BlockableEventLoop<?> workqueue = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
            CompletableFuture<Path> result = workqueue.isSameThread() ? CompletableFuture.completedFuture(job.call()) : CompletableFuture.supplyAsync(job::call, workqueue);
            return result.thenApply(path -> {
                loadedJobs.add(job.getClass());
                return path;
            });
        }
        if (getExecutor().isShutdown() || getExecutor().isTerminating() || getExecutor().isTerminated()) {
            return null;
        }
        return getExecutor().submit(job);
    }

    /**
     * Render debugging information for the pathfinding system.
     *
     * @param frame       entity movement weight.
     * @param matrixStack the matrix stack to apply to.
     */
    @OnlyIn(Dist.CLIENT)
    public static void debugDraw(final double frame, final PoseStack matrixStack) {
        if (AbstractPathJob.lastDebugNodesNotVisited == null) {
            return;
        }

        final Vec3 vec = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
        final double dx = vec.x();
        final double dy = vec.y();
        final double dz = vec.z();

        RenderSystem.pushTextureAttributes();

        matrixStack.pushPose();
        matrixStack.translate(-dx, -dy, -dz);

        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.disableLighting();

        final Set<Node> debugNodesNotVisited;
        final Set<Node> debugNodesVisited;
        final Set<Node> debugNodesPath;

        synchronized (debugNodeMonitor) {
            debugNodesNotVisited = AbstractPathJob.lastDebugNodesNotVisited;
            debugNodesVisited = AbstractPathJob.lastDebugNodesVisited;
            debugNodesPath = AbstractPathJob.lastDebugNodesPath;
        }

        try {
            for (final Node n : debugNodesNotVisited) {
                debugDrawNode(n, 1.0F, 0F, 0F, matrixStack);
            }

            for (final Node n : debugNodesVisited) {
                debugDrawNode(n, 0F, 0F, 1.0F, matrixStack);
            }

            for (final Node n : debugNodesPath) {
                if (n.isReachedByWorker()) {
                    debugDrawNode(n, 1F, 0.4F, 0F, matrixStack);
                } else {
                    debugDrawNode(n, 0F, 1.0F, 0F, matrixStack);
                }
            }
        } catch (final ConcurrentModificationException exc) {
            IceAndFire.LOGGER.catching(exc);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.popAttributes();
        matrixStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private static void debugDrawNode(final Node n, final float r, final float g, final float b, final PoseStack matrixStack) {
        matrixStack.pushPose();
        matrixStack.translate((double) n.pos.getX() + 0.375, (double) n.pos.getY() + 0.375, (double) n.pos.getZ() + 0.375);

        final Entity entity = Minecraft.getInstance().getCameraEntity();
        final double dx = n.pos.getX() - entity.getX();
        final double dy = n.pos.getY() - entity.getY();
        final double dz = n.pos.getZ() - entity.getZ();
        if (Math.sqrt(dx * dx + dy * dy + dz * dz) <= 5D) {
            renderDebugText(n, matrixStack);
        }

        matrixStack.scale(0.25F, 0.25F, 0.25F);

        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder vertexBuffer = tessellator.getBuilder();

        final Matrix4f matrix4f = matrixStack.last().pose();
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION);
        RenderSystem.color3f(r, g, b);

        //  X+
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 1.0f).endVertex();

        //  X-
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).endVertex();

        //  Z-
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 0.0f).endVertex();

        //  Z+
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 1.0f).endVertex();

        //  Y+
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 1.0f, 1.0f).endVertex();

        //  Y-
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 1.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 0.0f).endVertex();
        vertexBuffer.vertex(matrix4f, 1.0f, 0.0f, 1.0f).endVertex();

        tessellator.end();

        if (n.parent != null) {
            final float pdx = n.parent.pos.getX() - n.pos.getX() + 0.125f;
            final float pdy = n.parent.pos.getY() - n.pos.getY() + 0.125f;
            final float pdz = n.parent.pos.getZ() - n.pos.getZ() + 0.125f;
            vertexBuffer.begin(GL11.GL_LINES, DefaultVertexFormat.POSITION_COLOR);
            vertexBuffer.vertex(matrix4f, 0.5f, 0.5f, 0.5f).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            vertexBuffer.vertex(matrix4f, pdx / 0.25f, pdy / 0.25f, pdz / 0.25f).color(0.75F, 0.75F, 0.75F, 1.0F).endVertex();
            tessellator.end();
        }

        matrixStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private static void renderDebugText(final Node n, final PoseStack matrixStack) {
        final String s1 = String.format("F: %.3f [%d]", n.getCost(), n.getCounterAdded());
        final String s2 = String.format("G: %.3f [%d]", n.getScore(), n.getCounterVisited());
        final Font fontrenderer = Minecraft.getInstance().font;

        matrixStack.pushPose();
        matrixStack.translate(0.0F, 0.75F, 0.0F);
        RenderSystem.normal3f(0.0F, 1.0F, 0.0F);

        final EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        matrixStack.mulPose(renderManager.cameraOrientation());
        matrixStack.scale(-0.014F, -0.014F, 0.014F);
        matrixStack.translate(0.0F, 18F, 0.0F);

        RenderSystem.depthMask(false);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        RenderSystem.disableTexture();

        final int i = Math.max(fontrenderer.width(s1), fontrenderer.width(s2)) / 2;

        final Matrix4f matrix4f = matrixStack.last().pose();
        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder vertexBuffer = tessellator.getBuilder();
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
        vertexBuffer.vertex(matrix4f, (-i - 1), -5.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        vertexBuffer.vertex(matrix4f, (-i - 1), 12.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        vertexBuffer.vertex(matrix4f, (i + 1), 12.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        vertexBuffer.vertex(matrix4f, (i + 1), -5.0f, 0.0f).color(0.0F, 0.0F, 0.0F, 0.7F).endVertex();
        tessellator.end();

        RenderSystem.enableTexture();

        final MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        matrixStack.translate(0.0F, -5F, 0.0F);
        fontrenderer.drawInBatch(s1, -fontrenderer.width(s1) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);
        matrixStack.translate(0.0F, 8F, 0.0F);
        fontrenderer.drawInBatch(s2, -fontrenderer.width(s2) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);

        RenderSystem.depthMask(true);
        matrixStack.translate(0.0F, -8F, 0.0F);
        fontrenderer.drawInBatch(s1, -fontrenderer.width(s1) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);
        matrixStack.translate(0.0F, 8F, 0.0F);
        fontrenderer.drawInBatch(s2, -fontrenderer.width(s2) / 2.0f, 0, 0xFFFFFFFF, false, matrix4f, buffer, false, 0, 15728880);
        buffer.endBatch();

        matrixStack.popPose();
    }

    /**
     * Ice and Fire specific thread factory.
     */
    public static class IafThreadFactory implements ThreadFactory {
        /**
         * Ongoing thread IDs.
         */
        public static int id;

        @Override
        public Thread newThread(final Runnable runnable) {
            BlockableEventLoop<?> workqueue = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
            ClassLoader classLoader;
            if (workqueue.isSameThread()) {
                classLoader = Thread.currentThread().getContextClassLoader();
            } else {
                classLoader = CompletableFuture.supplyAsync(() -> Thread.currentThread().getContextClassLoader(), workqueue).join();
            }
            final Thread thread = new Thread(runnable, "Ice and Fire Pathfinding Worker #" + (id++));
            thread.setDaemon(true);
            thread.setPriority(Thread.MAX_PRIORITY);
            if (thread.getContextClassLoader() != classLoader) {
                IceAndFire.LOGGER.info("Corrected CCL of new Ice and Fire Pathfinding Thread, was: " + thread.getContextClassLoader().toString());
                thread.setContextClassLoader(classLoader);
            }
            thread.setUncaughtExceptionHandler((thread1, throwable) -> IceAndFire.LOGGER.error("Ice and Fire Pathfinding Thread errored! ", throwable));
            return thread;
        }
    }
}
