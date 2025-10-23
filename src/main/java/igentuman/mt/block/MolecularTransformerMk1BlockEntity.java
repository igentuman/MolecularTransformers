package igentuman.mt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MolecularTransformerMk1BlockEntity extends MolecularTransformerBlockEntity {
    private static final int MAX_ENERGY = 100000; // 100k FE capacity
    private static final int OUTPUT_COUNT = 1; // Produces 1 item

    public MolecularTransformerMk1BlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState, MAX_ENERGY, OUTPUT_COUNT);
    }
}