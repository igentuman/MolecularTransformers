package igentuman.mt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MolecularTransformerMk2BlockEntity extends MolecularTransformerBlockEntity {
    private static final int MAX_ENERGY = 500000; // 500k FE capacity
    private static final int OUTPUT_COUNT = 2; // Produces 2 items

    public MolecularTransformerMk2BlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState, MAX_ENERGY, OUTPUT_COUNT);
    }
}