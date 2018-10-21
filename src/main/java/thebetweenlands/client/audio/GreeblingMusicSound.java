package thebetweenlands.client.audio;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.lwjgl.openal.AL10;

import net.minecraft.util.SoundCategory;
import paulscode.sound.libraries.ChannelLWJGLOpenAL;
import thebetweenlands.api.audio.IEntitySound;
import thebetweenlands.client.handler.MusicHandler;
import thebetweenlands.common.entity.mobs.EntityGreebling;
import thebetweenlands.common.registries.SoundRegistry;

public class GreeblingMusicSound extends EntityMusicSound<EntityGreebling> {
	public final int type;

	private int ticksPlayed = 0;

	private boolean synced = false;

	private int syncTimer = 0;

	private volatile boolean isSoundReady = false;

	public GreeblingMusicSound(int type, EntityGreebling entity, float volume) {
		super(type == 0 ? SoundRegistry.GREEBLING_MUSIC_1 : SoundRegistry.GREEBLING_MUSIC_2, SoundCategory.NEUTRAL, entity, volume, AttenuationType.LINEAR);
		this.type = type;
	}

	@Override
	public void update() {
		if(!this.synced) {
			if(this.isSoundReady) {
				IEntitySound otherSound = MusicHandler.INSTANCE.getEntityMusic(this.type == 0 ? EntityMusicLayers.GREEBLING_2 : EntityMusicLayers.GREEBLING_1);

				if(otherSound instanceof GreeblingMusicSound) {
					try {
						Future<Float> secondsFuture = MusicHandler.INSTANCE.getOpenALAccess().getOffsetSeconds(otherSound);
						if(secondsFuture != null) {
							MusicHandler.INSTANCE.getOpenALAccess().setOffsetSeconds(this, secondsFuture.get());
						}
					} catch (InterruptedException | ExecutionException ex) {
						//Don't care, if it fails just play without sync
					}
				}

				this.synced = true;
			} else {
				if(this.syncTimer % 10 == 0) {
					final SoundSystemOpenALAccess openALAcess = MusicHandler.INSTANCE.getOpenALAccess();
					openALAcess.submitToSoundSystem(() -> {
						ChannelLWJGLOpenAL channel = openALAcess.getChannelAsync(this);

						if(channel != null && channel.ALSource != null) {
							int sourceId = channel.ALSource.get(0);
							if(sourceId >= 0) {
								int state = AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE);
								if(state == AL10.AL_PLAYING || state == AL10.AL_PAUSED) {
									this.isSoundReady = true;
								}
							}
						}
						return null;
					});
				}

				this.syncTimer++;
			}
		}

		this.ticksPlayed++;

		super.update();
	}
}
