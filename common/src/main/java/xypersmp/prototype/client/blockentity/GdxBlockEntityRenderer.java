/*
 * Copyright (c) 2024.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xypersmp.prototype.client.blockentity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.mcgdx.GdxMinecraft;
import dev.ultreon.mcgdx.api.Gdx3DRenderSource;
import dev.ultreon.mcgdx.impl.GdxBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class GdxBlockEntityRenderer<T extends GdxBlockEntity> implements BlockEntityRenderer<T>, Gdx3DRenderSource<T> {
    private final PerspectiveCamera camera = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    private final ModelBatch batch = new ModelBatch(new DefaultShaderProvider("""
            #if defined(diffuseTextureFlag) || defined(specularTextureFlag) || defined(emissiveTextureFlag)
            #define textureFlag
            #endif
            
            #if defined(specularTextureFlag) || defined(specularColorFlag)
            #define specularFlag
            #endif
            
            #if defined(specularFlag) || defined(fogFlag)
            #define cameraPositionFlag
            #endif
            
            attribute vec3 a_position;
            uniform mat4 u_projViewTrans;
            
            #if defined(colorFlag)
            varying vec4 v_color;
            attribute vec4 a_color;
            #endif // colorFlag
            
            #ifdef normalFlag
            attribute vec3 a_normal;
            uniform mat3 u_normalMatrix;
            varying vec3 v_normal;
            #endif // normalFlag
            
            #ifdef textureFlag
            attribute vec2 a_texCoord0;
            #endif // textureFlag
            
            #ifdef diffuseTextureFlag
            uniform vec4 u_diffuseUVTransform;
            varying vec2 v_diffuseUV;
            #endif
            
            #ifdef emissiveTextureFlag
            uniform vec4 u_emissiveUVTransform;
            varying vec2 v_emissiveUV;
            #endif
            
            #ifdef specularTextureFlag
            uniform vec4 u_specularUVTransform;
            varying vec2 v_specularUV;
            #endif
            
            #ifdef boneWeight0Flag
            #define boneWeightsFlag
            attribute vec2 a_boneWeight0;
            #endif //boneWeight0Flag
            
            #ifdef boneWeight1Flag
            #ifndef boneWeightsFlag
            #define boneWeightsFlag
            #endif
            attribute vec2 a_boneWeight1;
            #endif //boneWeight1Flag
            
            #ifdef boneWeight2Flag
            #ifndef boneWeightsFlag
            #define boneWeightsFlag
            #endif
            attribute vec2 a_boneWeight2;
            #endif //boneWeight2Flag
            
            #ifdef boneWeight3Flag
            #ifndef boneWeightsFlag
            #define boneWeightsFlag
            #endif
            attribute vec2 a_boneWeight3;
            #endif //boneWeight3Flag
            
            #ifdef boneWeight4Flag
            #ifndef boneWeightsFlag
            #define boneWeightsFlag
            #endif
            attribute vec2 a_boneWeight4;
            #endif //boneWeight4Flag
            
            #ifdef boneWeight5Flag
            #ifndef boneWeightsFlag
            #define boneWeightsFlag
            #endif
            attribute vec2 a_boneWeight5;
            #endif //boneWeight5Flag
            
            #ifdef boneWeight6Flag
            #ifndef boneWeightsFlag
            #define boneWeightsFlag
            #endif
            attribute vec2 a_boneWeight6;
            #endif //boneWeight6Flag
            
            #ifdef boneWeight7Flag
            #ifndef boneWeightsFlag
            #define boneWeightsFlag
            #endif
            attribute vec2 a_boneWeight7;
            #endif //boneWeight7Flag
            
            #if defined(numBones) && defined(boneWeightsFlag)
            #if (numBones > 0)\s
            #define skinningFlag
            #endif
            #endif
            
            uniform mat4 u_worldTrans;
            
            #if defined(numBones)
            #if numBones > 0
            uniform mat4 u_bones[numBones];
            #endif //numBones
            #endif
            
            #ifdef shininessFlag
            uniform float u_shininess;
            #else
            const float u_shininess = 20.0;
            #endif // shininessFlag
            
            #ifdef blendedFlag
            uniform float u_opacity;
            varying float v_opacity;
            
            #ifdef alphaTestFlag
            uniform float u_alphaTest;
            varying float v_alphaTest;
            #endif //alphaTestFlag
            #endif // blendedFlag
            
            #ifdef lightingFlag
            varying vec3 v_lightDiffuse;
            
            #ifdef ambientLightFlag
            uniform vec3 u_ambientLight;
            #endif // ambientLightFlag
            
            #ifdef ambientCubemapFlag
            uniform vec3 u_ambientCubemap[6];
            #endif // ambientCubemapFlag\s
            
            #ifdef sphericalHarmonicsFlag
            uniform vec3 u_sphericalHarmonics[9];
            #endif //sphericalHarmonicsFlag
            
            #ifdef specularFlag
            varying vec3 v_lightSpecular;
            #endif // specularFlag
            
            #ifdef cameraPositionFlag
            uniform vec4 u_cameraPosition;
            #endif // cameraPositionFlag
            
            #ifdef fogFlag
            varying float v_fog;
            #endif // fogFlag
            
            
            #if numDirectionalLights > 0
            struct DirectionalLight
            {
            	vec3 color;
            	vec3 direction;
            };
            uniform DirectionalLight u_dirLights[numDirectionalLights];
            #endif // numDirectionalLights
            
            #if numPointLights > 0
            struct PointLight
            {
            	vec3 color;
            	vec3 position;
            };
            uniform PointLight u_pointLights[numPointLights];
            #endif // numPointLights
            
            #if	defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)
            #define ambientFlag
            #endif //ambientFlag
            
            #ifdef shadowMapFlag
            uniform mat4 u_shadowMapProjViewTrans;
            varying vec3 v_shadowMapUv;
            #define separateAmbientFlag
            #endif //shadowMapFlag
            
            #if defined(ambientFlag) && defined(separateAmbientFlag)
            varying vec3 v_ambientLight;
            #endif //separateAmbientFlag
            
            #endif // lightingFlag
            
            void main() {
            	#ifdef diffuseTextureFlag
            		v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
            	#endif //diffuseTextureFlag
            
            	#ifdef emissiveTextureFlag
            		v_emissiveUV = u_emissiveUVTransform.xy + a_texCoord0 * u_emissiveUVTransform.zw;
            	#endif //emissiveTextureFlag
            
            	#ifdef specularTextureFlag
            		v_specularUV = u_specularUVTransform.xy + a_texCoord0 * u_specularUVTransform.zw;
            	#endif //specularTextureFlag
            
            	#if defined(colorFlag)
            		v_color = a_color;
            	#endif // colorFlag
            
            	#ifdef blendedFlag
            		v_opacity = u_opacity;
            		#ifdef alphaTestFlag
            			v_alphaTest = u_alphaTest;
            		#endif //alphaTestFlag
            	#endif // blendedFlag
            
            	#ifdef skinningFlag
            		mat4 skinning = mat4(0.0);
            		#ifdef boneWeight0Flag
            			skinning += (a_boneWeight0.y) * u_bones[int(a_boneWeight0.x)];
            		#endif //boneWeight0Flag
            		#ifdef boneWeight1Flag
            			skinning += (a_boneWeight1.y) * u_bones[int(a_boneWeight1.x)];
            		#endif //boneWeight1Flag
            		#ifdef boneWeight2Flag
            			skinning += (a_boneWeight2.y) * u_bones[int(a_boneWeight2.x)];
            		#endif //boneWeight2Flag
            		#ifdef boneWeight3Flag
            			skinning += (a_boneWeight3.y) * u_bones[int(a_boneWeight3.x)];
            		#endif //boneWeight3Flag
            		#ifdef boneWeight4Flag
            			skinning += (a_boneWeight4.y) * u_bones[int(a_boneWeight4.x)];
            		#endif //boneWeight4Flag
            		#ifdef boneWeight5Flag
            			skinning += (a_boneWeight5.y) * u_bones[int(a_boneWeight5.x)];
            		#endif //boneWeight5Flag
            		#ifdef boneWeight6Flag
            			skinning += (a_boneWeight6.y) * u_bones[int(a_boneWeight6.x)];
            		#endif //boneWeight6Flag
            		#ifdef boneWeight7Flag
            			skinning += (a_boneWeight7.y) * u_bones[int(a_boneWeight7.x)];
            		#endif //boneWeight7Flag
            	#endif //skinningFlag
            
            	#ifdef skinningFlag
            		vec4 pos = u_worldTrans * skinning * vec4(a_position, 1.0);
            	#else
            		vec4 pos = u_worldTrans * vec4(a_position, 1.0);
            	#endif
            
            	gl_Position = u_projViewTrans * pos;
            
            	#ifdef shadowMapFlag
            		vec4 spos = u_shadowMapProjViewTrans * pos;
            		v_shadowMapUv.xyz = (spos.xyz / spos.w) * 0.5 + 0.5;
            		v_shadowMapUv.z = min(v_shadowMapUv.z, 0.998);
            	#endif //shadowMapFlag
            
            	#if defined(normalFlag)
            		#if defined(skinningFlag)
            			vec3 normal = normalize((u_worldTrans * skinning * vec4(a_normal, 0.0)).xyz);
            		#else
            			vec3 normal = normalize(u_normalMatrix * a_normal);
            		#endif
            		v_normal = normal;
            	#endif // normalFlag
            
                #ifdef fogFlag
                    vec3 flen = u_cameraPosition.xyz - pos.xyz;
                    float fog = dot(flen, flen) * u_cameraPosition.w;
                    v_fog = min(fog, 1.0);
                #endif
            
            	#ifdef lightingFlag
            		#if	defined(ambientLightFlag)
                    	vec3 ambientLight = u_ambientLight;
            		#elif defined(ambientFlag)
                    	vec3 ambientLight = vec3(0.0);
            		#endif
            
            		#ifdef ambientCubemapFlag
            			vec3 squaredNormal = normal * normal;
            			vec3 isPositive  = step(0.0, normal);
            			ambientLight += squaredNormal.x * mix(u_ambientCubemap[0], u_ambientCubemap[1], isPositive.x) +
            					squaredNormal.y * mix(u_ambientCubemap[2], u_ambientCubemap[3], isPositive.y) +
            					squaredNormal.z * mix(u_ambientCubemap[4], u_ambientCubemap[5], isPositive.z);
            		#endif // ambientCubemapFlag
            
            		#ifdef sphericalHarmonicsFlag
            			ambientLight += u_sphericalHarmonics[0];
            			ambientLight += u_sphericalHarmonics[1] * normal.x;
            			ambientLight += u_sphericalHarmonics[2] * normal.y;
            			ambientLight += u_sphericalHarmonics[3] * normal.z;
            			ambientLight += u_sphericalHarmonics[4] * (normal.x * normal.z);
            			ambientLight += u_sphericalHarmonics[5] * (normal.z * normal.y);
            			ambientLight += u_sphericalHarmonics[6] * (normal.y * normal.x);
            			ambientLight += u_sphericalHarmonics[7] * (3.0 * normal.z * normal.z - 1.0);
            			ambientLight += u_sphericalHarmonics[8] * (normal.x * normal.x - normal.y * normal.y);
            		#endif // sphericalHarmonicsFlag
            
            		#ifdef ambientFlag
            			#ifdef separateAmbientFlag
            				v_ambientLight = ambientLight;
            				v_lightDiffuse = vec3(0.0);
            			#else
            				v_lightDiffuse = ambientLight;
            			#endif //separateAmbientFlag
            		#else
            	        v_lightDiffuse = vec3(0.0);
            		#endif //ambientFlag
            
            
            		#ifdef specularFlag
            			v_lightSpecular = vec3(0.0);
            			vec3 viewVec = normalize(u_cameraPosition.xyz - pos.xyz);
            		#endif // specularFlag
            
            		#if (numDirectionalLights > 0) && defined(normalFlag)
            			for (int i = 0; i < numDirectionalLights; i++) {
            				vec3 lightDir = -u_dirLights[i].direction;
            				float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
            				vec3 value = u_dirLights[i].color * NdotL;
            				v_lightDiffuse += value;
            				#ifdef specularFlag
            					float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));
            					v_lightSpecular += value * pow(halfDotView, u_shininess);
            				#endif // specularFlag
            			}
            		#endif // numDirectionalLights
            
            		#if (numPointLights > 0) && defined(normalFlag)
            			for (int i = 0; i < numPointLights; i++) {
            				vec3 lightDir = u_pointLights[i].position - pos.xyz;
            				float dist2 = dot(lightDir, lightDir);
            				lightDir *= inversesqrt(dist2);
            				float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
            				vec3 value = u_pointLights[i].color * (NdotL / (1.0 + dist2));
            				v_lightDiffuse += value;
            				#ifdef specularFlag
            					float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));
            					v_lightSpecular += value * pow(halfDotView, u_shininess);
            				#endif // specularFlag
            			}
            		#endif // numPointLights
            	#endif // lightingFlag
            }
            """, """
            #ifdef GL_ES
            #define LOWP lowp
            #define MED mediump
            #define HIGH highp
            precision mediump float;
            #else
            #define MED
            #define LOWP
            #define HIGH
            #endif
            
            #if defined(specularTextureFlag) || defined(specularColorFlag)
            #define specularFlag
            #endif
            
            #ifdef normalFlag
            varying vec3 v_normal;
            #endif //normalFlag
            
            #if defined(colorFlag)
            varying vec4 v_color;
            #endif
            
            #ifdef blendedFlag
            varying float v_opacity;
            #ifdef alphaTestFlag
            varying float v_alphaTest;
            #endif //alphaTestFlag
            #endif //blendedFlag
            
            #if defined(diffuseTextureFlag) || defined(specularTextureFlag) || defined(emissiveTextureFlag)
            #define textureFlag
            #endif
            
            #ifdef diffuseTextureFlag
            varying MED vec2 v_diffuseUV;
            #endif
            
            #ifdef specularTextureFlag
            varying MED vec2 v_specularUV;
            #endif
            
            #ifdef emissiveTextureFlag
            varying MED vec2 v_emissiveUV;
            #endif
            
            #ifdef diffuseColorFlag
            uniform vec4 u_diffuseColor;
            #endif
            
            #ifdef diffuseTextureFlag
            uniform sampler2D u_diffuseTexture;
            #endif
            
            #ifdef specularColorFlag
            uniform vec4 u_specularColor;
            #endif
            
            #ifdef specularTextureFlag
            uniform sampler2D u_specularTexture;
            #endif
            
            #ifdef normalTextureFlag
            uniform sampler2D u_normalTexture;
            #endif
            
            #ifdef emissiveColorFlag
            uniform vec4 u_emissiveColor;
            #endif
            
            #ifdef emissiveTextureFlag
            uniform sampler2D u_emissiveTexture;
            #endif
            
            #ifdef lightingFlag
            varying vec3 v_lightDiffuse;
            
            #if	defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)
            #define ambientFlag
            #endif //ambientFlag
            
            #ifdef specularFlag
            varying vec3 v_lightSpecular;
            #endif //specularFlag
            
            #ifdef shadowMapFlag
            uniform sampler2D u_shadowTexture;
            uniform float u_shadowPCFOffset;
            varying vec3 v_shadowMapUv;
            #define separateAmbientFlag
            
            float getShadowness(vec2 offset)
            {
                const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
                return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));
            }
            
            float getShadow()
            {
            	return (//getShadowness(vec2(0,0)) +
            			getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
            			getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
            			getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
            			getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
            }
            #endif //shadowMapFlag
            
            #if defined(ambientFlag) && defined(separateAmbientFlag)
            varying vec3 v_ambientLight;
            #endif //separateAmbientFlag
            
            #endif //lightingFlag
            
            #ifdef fogFlag
            uniform vec4 u_fogColor;
            varying float v_fog;
            #endif // fogFlag
            
            struct SHC{
                vec3 L00, L1m1, L10, L11, L2m2, L2m1, L20, L21, L22;
            };
            
            SHC groove = SHC(
                vec3( 0.3783264,  0.4260425,  0.4504587),
                vec3( 0.2887813,  0.3586803,  0.4147053),
                vec3( 0.0379030,  0.0295216,  0.0098567),
                vec3(-0.1033028, -0.1031690, -0.0884924),
                vec3(-0.0621750, -0.0554432, -0.0396779),
                vec3( 0.0077820, -0.0148312, -0.0471301),
                vec3(-0.0935561, -0.1254260, -0.1525629),
                vec3(-0.0572703, -0.0502192, -0.0363410),
                vec3( 0.0203348, -0.0044201, -0.0452180)
            );
            
            SHC beach = SHC(
                vec3( 0.6841148,  0.6929004,  0.7069543),
                vec3( 0.3173355,  0.3694407,  0.4406839),
                vec3(-0.1747193, -0.1737154, -0.1657420),
                vec3(-0.4496467, -0.4155184, -0.3416573),
                vec3(-0.1690202, -0.1703022, -0.1525870),
                vec3(-0.0837808, -0.0940454, -0.1027518),
                vec3(-0.0319670, -0.0214051, -0.0147691),
                vec3( 0.1641816,  0.1377558,  0.1010403),
                vec3( 0.3697189,  0.3097930,  0.2029923)
            );
            
            SHC tomb = SHC(
                vec3( 1.0351604,  0.7603549,  0.7074635),
                vec3( 0.4442150,  0.3430402,  0.3403777),
                vec3(-0.2247797, -0.1828517, -0.1705181),
                vec3( 0.7110400,  0.5423169,  0.5587956),
                vec3( 0.6430452,  0.4971454,  0.5156357),
                vec3(-0.1150112, -0.0936603, -0.0839287),
                vec3(-0.3742487, -0.2755962, -0.2875017),
                vec3(-0.1694954, -0.1343096, -0.1335315),
                vec3( 0.5515260,  0.4222179,  0.4162488)
            );
            
            vec3 sh_light(vec3 normal, SHC l){
                float x = normal.x;
                float y = normal.y;
                float z = normal.z;
            
                const float C1 = 0.429043;
                const float C2 = 0.511664;
                const float C3 = 0.743125;
                const float C4 = 0.886227;
                const float C5 = 0.247708;
            
                return (
                    C1 * l.L22 * (x * x - y * y) +
                    C3 * l.L20 * z * z +
                    C4 * l.L00 -
                    C5 * l.L20 +
                    2.0 * C1 * l.L2m2 * x * y +
                    2.0 * C1 * l.L21  * x * z +
                    2.0 * C1 * l.L2m1 * y * z +
                    2.0 * C2 * l.L11  * x +
                    2.0 * C2 * l.L1m1 * y +
                    2.0 * C2 * l.L10  * z
                );
            }
            
            vec3 gamma(vec3 color){
                return pow(color, vec3(1.0/2.0));
            }
            
            
            void main() {
            	#if defined(normalFlag)
            		vec3 normal = v_normal;
            	#endif // normalFlag
            
            	#if defined(diffuseTextureFlag) && defined(diffuseColorFlag) && defined(colorFlag)
            		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor * v_color;
            	#elif defined(diffuseTextureFlag) && defined(diffuseColorFlag)
            		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
            	#elif defined(diffuseTextureFlag) && defined(colorFlag)
            		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * v_color;
            	#elif defined(diffuseTextureFlag)
            		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);
            	#elif defined(diffuseColorFlag) && defined(colorFlag)
            		vec4 diffuse = u_diffuseColor * v_color;
            	#elif defined(diffuseColorFlag)
            		vec4 diffuse = u_diffuseColor;
            	#elif defined(colorFlag)
            		vec4 diffuse = v_color;
            	#else
            		vec4 diffuse = vec4(1.0);
            	#endif
            
                #ifdef normalFlag
                    diffuse.rgba = vec4(diffuse.xyz*gamma(sh_light(v_normal, groove)).r, diffuse.w);
                #endif
            
            	#if defined(emissiveTextureFlag) && defined(emissiveColorFlag)
            		vec4 emissive = texture2D(u_emissiveTexture, v_emissiveUV) * u_emissiveColor;
            	#elif defined(emissiveTextureFlag)
            		vec4 emissive = texture2D(u_emissiveTexture, v_emissiveUV);
            	#elif defined(emissiveColorFlag)
            		vec4 emissive = u_emissiveColor;
            	#else
            		vec4 emissive = vec4(0.0);
            	#endif
            
            	#if (!defined(lightingFlag))
            		gl_FragColor.rgb = diffuse.rgb + emissive.rgb;
            	#elif (!defined(specularFlag))
            		#if defined(ambientFlag) && defined(separateAmbientFlag)
            			#ifdef shadowMapFlag
            				gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + getShadow() * v_lightDiffuse)) + emissive.rgb;
            				//gl_FragColor.rgb = texture2D(u_shadowTexture, v_shadowMapUv.xy);
            			#else
            				gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + v_lightDiffuse)) + emissive.rgb;
            			#endif //shadowMapFlag
            		#else
            			#ifdef shadowMapFlag
            				gl_FragColor.rgb = getShadow() * (diffuse.rgb * v_lightDiffuse) + emissive.rgb;
            			#else
            				gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse) + emissive.rgb;
            			#endif //shadowMapFlag
            		#endif
            	#else
            		#if defined(specularTextureFlag) && defined(specularColorFlag)
            			vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb * u_specularColor.rgb * v_lightSpecular;
            		#elif defined(specularTextureFlag)
            			vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb * v_lightSpecular;
            		#elif defined(specularColorFlag)
            			vec3 specular = u_specularColor.rgb * v_lightSpecular;
            		#else
            			vec3 specular = v_lightSpecular;
            		#endif
            
            		#if defined(ambientFlag) && defined(separateAmbientFlag)
            			#ifdef shadowMapFlag
            			gl_FragColor.rgb = (diffuse.rgb * (getShadow() * v_lightDiffuse + v_ambientLight)) + specular + emissive.rgb;
            				//gl_FragColor.rgb = texture2D(u_shadowTexture, v_shadowMapUv.xy);
            			#else
            				gl_FragColor.rgb = (diffuse.rgb * (v_lightDiffuse + v_ambientLight)) + specular + emissive.rgb;
            			#endif //shadowMapFlag
            		#else
            			#ifdef shadowMapFlag
            				gl_FragColor.rgb = getShadow() * ((diffuse.rgb * v_lightDiffuse) + specular) + emissive.rgb;
            			#else
            				gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse) + specular + emissive.rgb;
            			#endif //shadowMapFlag
            		#endif
            	#endif //lightingFlag
            
            	#ifdef fogFlag
            		gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, v_fog);
            	#endif // end fogFlag
            
            	#ifdef blendedFlag
            		gl_FragColor.a = diffuse.a * v_opacity;
            		#ifdef alphaTestFlag
            			if (gl_FragColor.a <= v_alphaTest)
            				discard;
            		#endif
            	#else
            		gl_FragColor.a = 1.0;
            	#endif
            
            }
            """), new DefaultRenderableSorter() {

    });

    public static final Environment environment = new Environment();

    public GdxBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        environment.set(ColorAttribute.createAmbientLight(1, 1, 1, 1));
    }

    @Override
    public final void render(T blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        GdxMinecraft.setupCamera(camera, f, poseStack);

        batch.begin(camera);
        Gdx.gl.glCullFace(GL20.GL_BACK);
        RenderContext renderContext = batch.getRenderContext();
        renderContext.setBlending(false, 0, 0);
        renderContext.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        render(blockEntity);

        batch.end();
    }

    @Override
    public boolean shouldRender(T blockEntity, Vec3 vec3) {
        return true;
    }

    @Override
    public ModelBatch getBatch() {
        return batch;
    }

    @Override
    public void render(T holder) {

    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }
}
