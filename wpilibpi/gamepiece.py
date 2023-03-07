import numpy as np
import logging
from ntcore import NetworkTableInstance, NetworkTable, EventFlags

log = logging.getLogger(__name__)
log.addHandler(logging.NullHandler())

# this should be used as a singleton.  Notice it is initiazlied at the bottom of this file
# to use, just import like normal, the instance is the same name as the class
global gamepiecetracker  # this is defined below after the class as the singleton class instance


class GamePieceTracker:
    """
    This class tracks manages data tracking gamepieces on the field
    It is meant to be a singleton.
    Like the following variable declarations below, in python when variables are defined outside of __init__,
    they are not unique to a class instance
    """

    # the following are singletons
    gamepiece_results = np.zeros((3, 9, 2), dtype=float)
    gamepiece_chars = np.chararray((3, 9), unicode=True)
    gamepiece_chars[:] = "-"  # set default value to dash
    NTvar = NetworkTableInstance.getDefault().getTable("gridcam").getStringArrayTopic("grid").publish()
    NTreset = NetworkTableInstance.getDefault().getTable("gridcam").getBooleanTopic("grid_reset").getEntry(False)
    gamepiece_func = max

    # def register_NT_vars(self, ntable: NetworkTable):
    #     # self.reset_results()
    #     # TODO: add NETWORK TABLES SUPPORT HERE
    #     # self.NTvar = NetworkTableInstance.getDefault().getTable("gridcam").getStringArrayTopic("grid").publish()
    #     # self.NTreset = NetworkTableInstance.getDefault().getTable("gridcam").getBooleanTopic("grid_reset").getEntry(False)

    def reset_results(self):
        self.gamepiece_results[:] = 0
        self.gamepiece_chars[:] = "-"

    def map_gamepiece_results(self, tag_id: int, idx: int, yel_pct: float, vio_pct: float):
        """takes gamepiece result information and maps it to self.gamepiece_results array"""
        # from left to right tag locations
        # tag [3]  [2]  [1]
        # tag [8]  [7]  [6]
        if tag_id in [4, 5]:
            log.error(f"tag_id out of range, was {tag_id}")
            return
        if idx < 0 or idx > 8:  # bounds check
            log.error(f"idx out of range, was {idx}")
            return
        tag_map = {3: 0, 2: 3, 1: 6, 8: 0, 7: 3, 6: 6}
        tag_offset = tag_map[tag_id]
        col = tag_offset + idx // 3
        row = idx % 3
        log.debug(f"map_gamepiece:tag_id {tag_id},row = {row},col = {col},tag_offset = {tag_offset}")
        # write new values to gamepiece_results
        # TODO: make these values sticky
        prev_yel, prev_vio = self.gamepiece_results[row, col, :]
        self.gamepiece_results[row, col, :] = [
            self.gamepiece_func(prev_yel, yel_pct),
            self.gamepiece_func(prev_vio, vio_pct),
        ]

        self.NTvar.set(self.to_str())

        if self.NTreset.get():
            log.debug("GOT RESET FROM NETWORK TABLES")
            self.NTreset.set(False)
            self.reset_results()

    def to_str(self):
        # convert gamepiece to characters
        self.gamepiece_chars[self.gamepiece_results[:, :, 0] == 1] = "A"
        self.gamepiece_chars[self.gamepiece_results[:, :, 1] == 1] = "O"
        ret = ["".join(x) for x in self.gamepiece_chars]
        ret.reverse()  # so that when printing, bottom row is the bottom!
        log.info("\n%s", "\n".join(ret))
        return ret
        # return json.dumps({"gamepieces":["".join(x) for x in self.gamepiece_chars]})
        # return json.dumps({'cone':",".join([f"{x:0.2f}" for x in self.gamepiece_results[:,:,0].ravel()]),
        #                   'cube':self.gamepiece_results[:,:,1].tolist()},
        #                   separators=(',',':'))


gamepiecetracker = GamePieceTracker()
