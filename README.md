# Crop's Power

庄稼的力量，我知道！

本mod给[CropsNH](https://github.com/GTNewHorizons/CropsNH)加了一点带劲的玩(私)意(货)

包括：

1. 扩大自动作物管理机的工作范围（允许自定义水平半径与垂直高度）
2. 允许自动作物管理机收获种子（原版默认是不会收割到种子的，未免也太坐牢了，不爽？不爽就手搓私货！）
3. 延迟作物架缓存更新（避免有人设置了超大范围过于影响TPS）

---

## 安装

[![最新构建(java)](https://img.shields.io/github/actions/workflow/status/Rcrwrate/CropsPower/build-and-test.yml?logo=github&label=Build%20and%20test)](https://github.com/Rcrwrate/CropsPower/actions/workflows/build-and-test.yml)
[![最新发布](https://img.shields.io/github/v/release/Rcrwrate/CropsPower)](https://github.com/Rcrwrate/CropsPower/releases/latest)

![Docker Last Updated](https://img.shields.io/docker/last-updated/shirokasoke/mcwebapi?logo=docker&label=Env%20Last%20Updated)
![Docker size](https://img.shields.io/docker/image-size/shirokasoke/mcwebapi)
![Repo size](https://img.shields.io/github/repo-size/Rcrwrate/CropsPower)

目前状态：

在一定程度下向下兼容

| GTNHLib版本 | CropsNH版本 | 最后版本 |
| ----------- | ----------- | -------- |
| 0.11.37     | 2.0.114     | v0.1     |

## 其他mod

[![](https://img.shields.io/github/v/release/Rcrwrate/MCWebAPI?logo=github&label=MCWebAPI)](https://github.com/Rcrwrate/MCWebAPI)
[![](https://img.shields.io/github/v/release/Rcrwrate/AggressivePatch?logo=github&label=AggressivePatch)](https://github.com/Rcrwrate/AggressivePatch)
[![](https://img.shields.io/github/v/release/Rcrwrate/CropsPower?logo=github&label=CropsPower)](https://github.com/Rcrwrate/CropsPower)

## 设置详解

配置文件：`config/shirokasoke/CropsPower.cfg`（首次启动时生成），内部分为 `mixin` 与 `asm` 两个分类。

### mixin

| 配置项                                 | 默认值 | 可选范围 | 说明                                           |
| -------------------------------------- | ------ | -------- | ---------------------------------------------- |
| `B:dropSeed`                           | `true` | —        | 收获时是否额外掉落作物的种子，默认开启         |
| `I:cropManagerHorizontalRadiusBase`    | `3`    | 0 ~ 64   | 水平工作半径的基础值，原版为 3                 |
| `I:cropManagerHorizontalRadiusPerTier` | `2`    | 0 ~ 64   | 每提升一个电压等级额外增加的水平半径，原版为 2 |
| `I:cropManagerVerticalRadius`          | `2`    | 0 ~ 64   | 垂直工作半径（向上、向下各多少格），原版为 2   |

工作范围按机器的电压等级（tier）计算：

- 水平半径 = `cropManagerHorizontalRadiusBase + cropManagerHorizontalRadiusPerTier × tier`
- 垂直半径 = `cropManagerVerticalRadius`
- 实际覆盖 = `(2 × 水平半径 + 1)²` 格的水平面积 × `(2 × 垂直半径 + 1)` 格的高度

举例：默认LV(1) 为 11×11×5，每级水平 +2

> 范围扩大后，单次缓存扫描要遍历的方块数按立方增长，机器的性能开销也水涨船高（但是也没那么高），可用配合下面的 `cropManagerCacheRefreshMultiplier` 一起调整。

### asm

| 配置项                                | 类型 | 默认值 | 可选范围 | 说明                                        |
| ------------------------------------- | ---- | ------ | -------- | ------------------------------------------- |
| `I:cropManagerCacheRefreshMultiplier` | 整数 | `12`   | 10 ~ 30  | 缓存中含有作物架时的刷新间隔倍率，原版为 12 |

管理机自身的作业循环固定为 50 tick（可能根据CropsNH版本不同而不同），缓存刷新间隔 = `50 tick × 该倍率`（20 tick = 1 秒）：

- 缓存为空时：固定 100 tick 刷新一次，不受本项影响，所以工作范围内一个作物架都没有时依然能较快地重新扫描；
- 缓存非空时：默认 600 tick（30 秒）刷新一次；倍率越高越省性能（10 → 25 秒，30 → 75 秒），但新放下的作物架要等更久才会被纳入缓存、开始作业。

算了算感觉好像没必要，算了，写都写了，放这吧
